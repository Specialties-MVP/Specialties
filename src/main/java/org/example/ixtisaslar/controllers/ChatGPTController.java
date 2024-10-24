package org.example.ixtisaslar.controllers;

import org.example.ixtisaslar.services.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatgpt")
public class ChatGPTController {
    private final ChatGPTService chatGPTService;

    @Autowired
    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping("/ask")
    public String askChatGPT(@RequestBody String question) {
        try {
            return chatGPTService.askQuestion(question);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
