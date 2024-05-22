package com.socialnetwork.socialnetwork.service;

import io.awspring.cloud.s3.S3Template;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

@Service
public class S3Service {
    private final S3Template s3Template;

    private String bucketName = "social-network-storage";

    public S3Service(S3Template s3Template) {
        this.s3Template = s3Template;
    }

    public String createPresignedDownloadUrl(String key) {
        return s3Template.createSignedGetURL(bucketName, key, Duration.ofMinutes(10)).toExternalForm();
    }

    public String uploadToBucket(MultipartFile file) {
        String key = file.getOriginalFilename();
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        try {
            s3Template.upload(bucketName, key, file.getInputStream());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return key;
    }


}
