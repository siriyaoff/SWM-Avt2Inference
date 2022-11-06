package com.avatar2.inference.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@CrossOrigin(origins="http://localhost:8080, " +
        "http://localhost:3000, " +
        "http://ec2-13-124-191-61.ap-northeast-2.compute.amazonaws.com:8080, " +
        "http://ec2-13-209-174-9.ap-northeast-2.compute.amazonaws.com:8080", allowedHeaders="*")
public class CorstestController {
    @PostMapping("/stringtest")
    public String stringtest(@RequestBody String req) {
        return req;
    }

    @GetMapping("/callstringtest")
    public String callstr() {
        WebClient webClient = WebClient.create("http://localhost:8080");
        return webClient.post()
                .uri("/stringtest")
                //.contentType(MediaType.APPLICATION_JSON)
                .bodyValue("testString")
                .retrieve()
                .bodyToMono(String.class)// response 파일
                .block();
    }
}
