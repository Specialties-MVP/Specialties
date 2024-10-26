package org.example.ixtisaslar.controllers;

import org.example.ixtisaslar.services.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // Yeni PDF upload endpointi
    @PostMapping(value = "/upload-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            String analysisMessage = chatGPTService.analyzePdf(file);
            return ResponseEntity.ok(analysisMessage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
//test