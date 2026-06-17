package com.AIService.AIService.controller;

import com.AIService.AIService.dto.ATSResponseDto;
import com.AIService.AIService.service.AtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AtsController {

    private final AtsService atsService;

    @PostMapping(
            value = "/analyze",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ATSResponseDto analyzeResume(

            @RequestParam("resume")
            MultipartFile resume,

            @RequestParam("jobDescription")
            String jobDescription
    ) {

        return atsService.analyzeResume(
                resume,
                jobDescription
        );
    }
}
