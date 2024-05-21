package com.socialnetwork.socialnetwork.service;

import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.internal.sync.FileContentStreamProvider;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

@Service
public class S3Service {
    private final S3Template s3Template;
//    private final S3Presigner s3Presigner;

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

    public void uploadToBucket(File file) {
        String key = file.getName();
        String url = createPresignedUrl(key);
        useHttpClientToPut(url,file);
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
