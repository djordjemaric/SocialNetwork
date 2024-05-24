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
import org.springframework.ai.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final ImageClient imageClient;

    public AIService(ChatClient chatClient, ImageClient imageClient) {
        this.chatClient = chatClient;
        this.imageClient = imageClient;
    }

    public String generateText(String prompt){
        return chatClient.call(new Prompt(prompt)).getResult().getOutput().getContent();
    }

    public MultipartFile generateImg(String prompt) {
        try { ImageResponse imageResponse=imageClient.call(
                new ImagePrompt(prompt,
                        OpenAiImageOptions.builder()
                                .withQuality("hd")
                                .withN(4)
                                .withHeight(1024)
                                .withWidth(1024)
                                .build())
        );

            return convertToMultipartFile(convertToAwtImage(imageResponse.getResult().getOutput()),prompt,"image/jpeg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultipartFile convertToMultipartFile(java.awt.Image image, String fileName, String contentType) throws IOException {
        BufferedImage bufferedImage;
        if (image instanceof BufferedImage) {
            bufferedImage = (BufferedImage) image;
        } else {
            bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);

        return new MockMultipartFile(fileName, fileName, contentType, baos.toByteArray());
    }
    public static BufferedImage convertToAwtImage(org.springframework.ai.image.Image springImage) throws IOException {
        String b64Json= springImage.getB64Json();
        byte[] imageData = Base64.getDecoder().decode(b64Json);

        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        BufferedImage awtImage = ImageIO.read(bais);

        return awtImage;
    }

}
