package com.avatar2.inference.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080",
                        "https://localhost:8080",
                        "http://localhost:3000",
                        "https://localhost:3000",
                        "http://ec2-13-124-191-61.ap-northeast-2.compute.amazonaws.com:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
