package com.avatar2.inference.web;

import com.avatar2.inference.web.dto.InferenceRequestDto;
import com.avatar2.inference.web.dto.InferenceResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InferenceController {
    @PostMapping("/inference")
    public InferenceResponseDto inference(@RequestBody InferenceRequestDto inferenceRequestDto) {

    }
}
