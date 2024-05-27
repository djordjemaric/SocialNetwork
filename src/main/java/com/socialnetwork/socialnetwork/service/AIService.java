package com.socialnetwork.socialnetwork.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatClient chatClient;

    public AIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String generateText(String prompt){
        return chatClient.call(new Prompt(prompt)).getResult().getOutput().getContent();
    }


}
