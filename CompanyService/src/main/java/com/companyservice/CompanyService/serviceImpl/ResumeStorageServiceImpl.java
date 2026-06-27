package com.companyservice.CompanyService.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ResumeStorageServiceImpl {

    private final Path uploadPath;

    public ResumeStorageServiceImpl(
            @Value("${file.upload-dir:uploads/applications/resumes}") String uploadDir
    ) {

        this.uploadPath =
                Paths.get(uploadDir)
                        .toAbsolutePath()
                        .normalize();
    }

    public String uploadResume(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Resume file is required"
            );
        }

        try {
            Files.createDirectories(uploadPath);

            String originalFileName =
                    file.getOriginalFilename();

            String extension =
                    getExtension(originalFileName);

            String fileName =
                    UUID.randomUUID() + extension;

            Path targetPath =
                    uploadPath.resolve(fileName);

            file.transferTo(targetPath);

            return targetPath.toString();
        } catch (IOException exception) {
            log.error(
                    "Error uploading application resume",
                    exception
            );

            throw new RuntimeException(
                    "Error uploading resume"
            );
        }
    }

    private String getExtension(
            String fileName
    ) {

        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(
                fileName.lastIndexOf(".")
        );
    }
}
