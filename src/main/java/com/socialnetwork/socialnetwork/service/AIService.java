package com.socialnetwork.socialnetwork.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final ImageClient imageClient;

    public AIService(ChatClient chatClient, ImageClient imageClient) {
        this.chatClient = chatClient;
        this.imageClient = imageClient;
    }

    public String generateText(String prompt) {
        return chatClient.call(new Prompt(prompt)).getResult().getOutput().getContent();
    }

    public InputStream generateImg(String prompt) {
        try {
            ImageResponse imageResponse = imageClient.call(
                    new ImagePrompt(prompt,
                            OpenAiImageOptions.builder()
                                    .withQuality("hd")
                                    .withN(1)
                                    .withHeight(1024)
                                    .withWidth(1024)
                                    .build())
            );

            return downloadImageFromUrl(imageResponse.getResult().getOutput().getUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultipartFile convertToMultipartFile(InputStream inputStream, String fileName, String contentType) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "image/jpg", baos);

        return new MockMultipartFile(fileName, fileName, contentType, baos.toByteArray());
    }

    public static InputStream downloadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        return connection.getInputStream();
    }
}
