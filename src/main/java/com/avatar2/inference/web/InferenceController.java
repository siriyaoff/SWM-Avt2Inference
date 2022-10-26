package com.avatar2.inference.web;

import com.avatar2.inference.domain.inference.PElem;
import com.avatar2.inference.domain.shellrun.StreamGobbler;
import com.avatar2.inference.web.dto.InferenceRequestDto;
import com.avatar2.inference.web.dto.InferenceResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Executors;

@RestController
public class InferenceController {
    @PostMapping("/inference")
    public InferenceResponseDto inference(@RequestBody InferenceRequestDto inferenceRequestDto) throws IOException, InterruptedException, FileNotFoundException {
        InferenceResponseDto inferenceResponseDto = null;
        try {
            Path path = Paths.get("/home/ec2-user").resolve("tpsd.psd");
            inferenceRequestDto.getPsd().transferTo(path);

            InferenceController.processBuilder();

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

            File file = new File("/home/ec2-user/iris.png");
            FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            MultipartFile iris = new CommonsMultipartFile(fileItem);
            inferenceResponseDto = new InferenceResponseDto(coord, eyelash, iris, "");
        } catch (Exception e) {
            String err=e.getStackTrace()[0].toString();
            return new InferenceResponseDto(null, null, null, err);
        }
        return inferenceResponseDto;
    }

    @GetMapping("/test")
    public String test() throws IOException, InterruptedException {
        InferenceResponseDto inferenceResponseDto = null;

        // read psd as File, convert to multipartfile
        try {
            File file = new File("/Users/yanghyowon/Downloads/tpsd.psd");
            FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            MultipartFile iris = new CommonsMultipartFile(fileItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ArrayList<PElem> coord, eyelash;
            JSONParser parser = new JSONParser();
            JSONObject ob, elemob;
            JSONArray point;
            ObjectMapper objectMapper = new ObjectMapper();

            Reader reader = new FileReader("/Users/yanghyowon/Downloads/testPoint.json");
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

            reader = new FileReader("/Users/yanghyowon/Downloads/testEyelash.json");
            ob = (JSONObject) parser.parse(reader);
            point = (JSONArray) parser.parse(ob.get("Point").toString());
            eyelash = new ArrayList<>();
            for (Object obj : point) {
                elemob = (JSONObject) obj;
                eyelash.add(PElem.builder()
                        .id((String) elemob.get("id"))
                        .x((String) elemob.get("x"))
                        .y((String) elemob.get("y"))
                        .build());
            }

            File file = new File("/Users/yanghyowon/Downloads/iris.png");
            FileItem fileItem = new DiskFileItem("originFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            MultipartFile iris = new CommonsMultipartFile(fileItem);
            inferenceResponseDto = new InferenceResponseDto(coord, eyelash, iris, "");



            Path path = Paths.get("/Users/yanghyowon/Downloads").resolve("serveriris.png");
            iris.transferTo(path);

        } catch (Exception e) {
            inferenceResponseDto=new InferenceResponseDto(null, null, null, e.getStackTrace()[0].toString());
        }
        if(inferenceResponseDto.getErr()=="")
            return inferenceResponseDto.getCoord().get(1).toString();
        else return inferenceResponseDto.getErr();
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
