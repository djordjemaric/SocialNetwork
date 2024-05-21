package com.socialnetwork.socialnetwork.service;

import io.awspring.cloud.s3.S3Template;
import org.springframework.stereotype.Service;

import java.io.File;
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

    private String createPresignedUrl(String key) {
        return s3Template.createSignedPutURL(bucketName, key, Duration.ofMinutes(10)).toExternalForm();
    }

    public String uploadToBucket(File file) {
        String key = file.getName();
        String url = createPresignedUrl(key);
        useHttpClientToPut(url,file);
        return key;
    }

    private void useHttpClientToPut(String presignedUrlString, File fileToPut) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            httpClient.send(requestBuilder
                            .uri(new URL(presignedUrlString).toURI())
                            .PUT(HttpRequest.BodyPublishers.ofFile(Path.of(fileToPut.toURI())))
                            .build(),
                    HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
