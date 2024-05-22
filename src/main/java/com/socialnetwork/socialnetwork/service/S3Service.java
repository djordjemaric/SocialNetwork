package com.socialnetwork.socialnetwork.service;

import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3Service {
    private final S3Template s3Template;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Template s3Template) {
        this.s3Template = s3Template;
    }

    public String createPresignedDownloadUrl(String key) {
        return s3Template.createSignedGetURL(bucketName, key, Duration.ofMinutes(10)).toExternalForm();
    }

    public String uploadToBucket(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String extension = filename
                    .substring(filename.lastIndexOf("."));
            String key = UUID.randomUUID() + extension;
            s3Template.upload(bucketName, key, file.getInputStream());
            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFromBucket(String key) {
        s3Template.deleteObject(bucketName, key);
    }




}
