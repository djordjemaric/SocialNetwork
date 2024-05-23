package com.socialnetwork.socialnetwork.controller;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/prompt")
@RestController
public class PromptController {
    private final ChatClient chatClient;

    public PromptController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    @GetMapping()
    public String generatePrompt(){
        return chatClient.call(new Prompt("tell me a dad joke")).getResult().getOutput().getContent();
    }

}
