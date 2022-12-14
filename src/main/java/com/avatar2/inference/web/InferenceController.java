package com.avatar2.inference.web;

import com.avatar2.inference.domain.inference.PElem;
import com.avatar2.inference.domain.shellrun.StreamGobbler;
import com.avatar2.inference.web.dto.InferenceResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Executors;

@RestController
@CrossOrigin(origins="http://localhost:8080, " +
        "http://localhost:3000, " +
        "http://ec2-13-124-191-61.ap-northeast-2.compute.amazonaws.com:8080, " +
        "http://ec2-13-209-174-9.ap-northeast-2.compute.amazonaws.com:8080", allowedHeaders="*")
public class InferenceController {
    /**
     * inference request with reqdto
     * @param req reqdto
     * @return resdto
     */
    @PostMapping("/inference")
    public InferenceResponseDto inference(MultipartHttpServletRequest req) {
        InferenceResponseDto inferenceResponseDto = null;
        String log="";
        try {
//            Path path = Paths.get("/home/ec2-user").resolve("tpsd.psd");
//            req.getFile("file").transferTo(path);
            MultipartFile psd = req.getFile("file");
            FileUtils.writeByteArrayToFile(new File("/home/ec2-user/tpsd.psd"),
                    psd.getBytes());

            log+="1";
            System.out.println("On /inference :: psd saved");

            InferenceController.processBuilder();

            log+="2";
            System.out.println("On /inference :: execInf.sh executed");

            ArrayList<PElem> coord, eyelash;
            JSONParser parser = new JSONParser();
            JSONObject jsonObject, jsonObject1;
            JSONArray pointArray;

            Reader reader = new FileReader("/home/ec2-user/coord.json");
            jsonObject = (JSONObject) parser.parse(reader);
            pointArray = (JSONArray) parser.parse(jsonObject.get("Point").toString());
            coord = new ArrayList<>();
            for (Object obj : pointArray) {
                jsonObject1 = (JSONObject) obj;
                coord.add(PElem.builder()
                        .id((String) jsonObject1.get("id"))
                        .x((String) jsonObject1.get("x"))
                        .y((String) jsonObject1.get("y"))
                        .build());
            }

            reader = new FileReader("/home/ec2-user/eyelash.json");
            jsonObject = (JSONObject) parser.parse(reader);
            pointArray = (JSONArray) parser.parse(jsonObject.get("Point").toString());
            eyelash = new ArrayList<>();
            for (Object obj : pointArray) {
                jsonObject1 = (JSONObject) obj;
                eyelash.add(PElem.builder()
                        .id((String) jsonObject1.get("id"))
                        .x((String) jsonObject1.get("x"))
                        .y((String) jsonObject1.get("y"))
                        .build());
            }

            log+="3";
            System.out.println("On /inference :: json parsed");

            File file = new File("/home/ec2-user/iris.png");
            FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            MultipartFile iris = new CommonsMultipartFile(fileItem);

            log+="4";
            System.out.println("On /inference :: png parsed");

            inferenceResponseDto = new InferenceResponseDto(coord, eyelash, iris.getBytes(), log);

            System.out.println("On /inference :: resdto created");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.append("************************************");
            e.printStackTrace(pw);
            pw.append("************************************");
            pw.append(e.getMessage());
            return new InferenceResponseDto(null, null, null, log+sw.toString());
        }
        return inferenceResponseDto;
    }

    /**
     * parse json, png and create resdt
     * @return resdto
     */
    @GetMapping("/rett")
    public InferenceResponseDto rett() {
        InferenceResponseDto inferenceResponseDto = null;
        try {
            ArrayList<PElem> coord, eyelash;
            JSONParser parser = new JSONParser();
            JSONObject jsonObject, jsonObject1;
            JSONArray pointArray;
            Reader reader;
            boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("windows");

            if(isWin) reader = new FileReader("C:\\Users\\yang\\Downloads\\testPoint.json");
            else reader = new FileReader("/Users/yanghyowon/Downloads/testPoint.json");
            jsonObject = (JSONObject) parser.parse(reader);
            pointArray = (JSONArray) parser.parse(jsonObject.get("Point").toString());
            coord = new ArrayList<>();
            for (Object obj : pointArray) {
                jsonObject1 = (JSONObject) obj;
                coord.add(PElem.builder()
                        .id((String) jsonObject1.get("id"))
                        .x((String) jsonObject1.get("x"))
                        .y((String) jsonObject1.get("y"))
                        .build());
            }

            if(isWin) reader = new FileReader("C:\\Users\\yang\\Downloads\\testEyelash.json");
            else reader = new FileReader("/Users/yanghyowon/Downloads/testEyelash.json");
            jsonObject = (JSONObject) parser.parse(reader);
            pointArray = (JSONArray) parser.parse(jsonObject.get("Point").toString());
            eyelash = new ArrayList<>();
            for (Object obj : pointArray) {
                jsonObject1 = (JSONObject) obj;
                eyelash.add(PElem.builder()
                        .id((String) jsonObject1.get("id"))
                        .x((String) jsonObject1.get("x"))
                        .y((String) jsonObject1.get("y"))
                        .build());
            }

            File file;
            if(isWin) file = new File("C:\\Users\\yang\\Downloads\\iris.png");
            else file = new File("/Users/yanghyowon/Downloads/iris.png");
            FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            MultipartFile iris = new CommonsMultipartFile(fileItem);
            inferenceResponseDto = new InferenceResponseDto(coord, eyelash, iris.getBytes(), "");
        } catch (Exception e) {
            String err = e.getStackTrace()[0].toString();
            return new InferenceResponseDto(null, null, null, err);
        }
        return inferenceResponseDto;
    }

    /**
     * request /rett, receive resdto
     * @return resdto.getErr()
     */
    @GetMapping("/rest") // request /rett
    public String rest() {
        String res;
        try {
            ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(300 * 1024 * 1024))
                    .build();
            WebClient webClient = WebClient.builder()
                    .exchangeStrategies(exchangeStrategies)
                    .baseUrl("http://localhost:8080")
                    .build();
            InferenceResponseDto responseDto = webClient.get()
                    .uri("/rett")
                    .retrieve()
                    .bodyToMono(InferenceResponseDto.class)
                    .block();
            System.out.println("return to rest");
            if(responseDto==null) res = "dto is null!!!!!!!!";
            else res = "Succeed, " + responseDto.getErr();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.append("************************************");
            e.printStackTrace(pw);
            pw.append("************************************");
            pw.append(e.getMessage());
            res = sw.toString();
        }
        return res;
    }

    @GetMapping("/json") // parse json, png to resdto
    public String json() throws IOException, ParseException {
        ArrayList<PElem> coord, eyelash;
        JSONParser parser = new JSONParser();
        JSONObject ob, elemob;
        JSONArray point;
        ObjectMapper objectMapper = new ObjectMapper();
        Reader reader;
        boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("windows");

        if(isWin) reader = new FileReader("C:\\Users\\yang\\Downloads\\testPoint.json");
        else reader = new FileReader("/Users/yanghyowon/Downloads/testPoint.json");
        ob = (JSONObject) parser.parse(reader);
        point = (JSONArray) parser.parse(ob.get("Point").toString());
        coord = new ArrayList<>();
        for (Object obj : point) {
            elemob = (JSONObject) obj;
            coord.add(PElem.builder()
                    .id((String) elemob.get("id"))
                    .x((String) elemob.get("x"))
                    .y((String) elemob.get("y"))
                    .build());
        }

        File file;
        if(isWin) file = new File("C:\\Users\\yang\\Downloads\\iris.png");
        else file=new File("/Users/yanghyowon/Downloads/iris.png");
        FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
        InputStream input = new FileInputStream(file);
        OutputStream os = fileItem.getOutputStream();
        IOUtils.copy(input, os);
        MultipartFile iris = new CommonsMultipartFile(fileItem);
        InferenceResponseDto inferenceResponseDto = new InferenceResponseDto(coord, coord, iris.getBytes(), "");

        String res = objectMapper.writeValueAsString(inferenceResponseDto.getCoord());

        return res;
    }

    public static void processBuilder() throws IOException, InterruptedException {
        String homeDir = System.getProperty("user.home");
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(homeDir));
        //builder.command("sh", "-c", "/opt/conda/envs/inf1/bin/python3.9 /home/ec2-user/tpy.py");
        builder.command("sh", "-c", "./execInf.sh");

        Process process = builder.start();
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);

        int exitCode = process.waitFor();
        assert exitCode == 0;
    }
}
