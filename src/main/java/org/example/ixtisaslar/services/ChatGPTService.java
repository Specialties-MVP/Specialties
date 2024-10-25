package org.example.ixtisaslar.services;

import org.springframework.web.multipart.MultipartFile;

public interface ChatGPTService {
    String askQuestion(String question) throws Exception;
    String analyzePdf(MultipartFile file) throws Exception;
}
