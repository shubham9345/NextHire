package com.UserService.UserService.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String uploadResume(
            MultipartFile file
    ) {

        try {

            String originalFileName =
                    file.getOriginalFilename();

            String fileExtension =
                    originalFileName.substring(
                            originalFileName.lastIndexOf(".")
                    );

            String uniqueFileName =
                    UUID.randomUUID() + fileExtension;

            Path uploadPath =
                    Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {

                Files.createDirectories(uploadPath);
            }

            Path filePath =
                    uploadPath.resolve(uniqueFileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            log.info(
                    "Resume uploaded successfully: {}",
                    uniqueFileName
            );

            return filePath.toString();

        } catch (IOException e) {

            log.error(
                    "Error uploading resume",
                    e
            );

            throw new RuntimeException(
                    "Could not upload file"
            );
        }
    }
}