package com.avatar2.inference.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class InferenceRequestDto {
    //private Long id;
    private int height;
    private MultipartFile psd;
    private Cookie cookie;


    @Builder
    public InferenceRequestDto(int height, MultipartFile psd){
        //this.id = id;
        this.height = height;
        this.psd = psd;
        this.cookie=new Cookie("time", LocalTime.now().toString());
    }
}
