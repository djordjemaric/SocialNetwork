package com.socialnetwork.socialnetwork.service;

import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
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

    public String uploadToBucket(String fileExtension, InputStream stream) {
        String fileKey = UUID.randomUUID() + fileExtension;
        s3Template.upload(bucketName, fileKey, stream);
        return fileKey;
    }


    public void deleteFromBucket(String key) {
        s3Template.deleteObject(bucketName, key);
    }




}
