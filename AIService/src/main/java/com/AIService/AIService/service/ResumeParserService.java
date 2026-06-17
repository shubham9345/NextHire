package com.AIService.AIService.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class ResumeParserService {

    public String extractText(
            MultipartFile file
    ) {

        try {

            PDDocument document =
                    Loader.loadPDF(
                            file.getBytes()
                    );

            PDFTextStripper stripper =
                    new PDFTextStripper();

            String text =
                    stripper.getText(document);

            document.close();

            log.info(
                    "Resume parsed successfully"
            );

            return text;

        } catch (IOException e) {

            log.error(
                    "Error parsing PDF",
                    e
            );

            throw new RuntimeException(
                    "Failed to parse resume"
            );
        }
    }
}