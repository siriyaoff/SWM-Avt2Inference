package com.avatar2.inference.web;

import com.avatar2.inference.domain.shellrun.StreamGobbler;
import com.avatar2.inference.web.dto.InferenceRequestDto;
import com.avatar2.inference.web.dto.InferenceResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

@RestController
public class InferenceController {
    @PostMapping("/inference")
    public String  inference(@RequestBody InferenceRequestDto inferenceRequestDto) {// InferenceResponseDto
        return "inference request";
    }


    public static void processBuilder() throws IOException, InterruptedException {
        String homeDir = System.getProperty("user.home");
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(homeDir));
        //builder.command("sh", "-c", "/opt/conda/envs/inf1/bin/python3.9 /home/ec2-user/tpy.py");
        builder.command("sh", "-c", "ps -al");

        Process process = builder.start();
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);

        int exitCode = process.waitFor();
        assert exitCode == 0;
    }
}
