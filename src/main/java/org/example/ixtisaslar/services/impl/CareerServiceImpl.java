package org.example.ixtisaslar.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.example.ixtisaslar.services.CareerService;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CareerServiceImpl  implements CareerService {
    private static final String API_URL = "AI_API_ENDPOINT";
    private static final String API_KEY = "YOUR_API_KEY";
    @Override
    public String analyzeCareerPath(String userAnswers) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<String> request = new HttpEntity<>(userAnswers, headers);

        return restTemplate.postForObject(API_URL, request, String.class);
    }

    public List<TypePatternQuestions.Question> loadQuestionsFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(
                    new File("src/main/resources/questions.json"), TypePatternQuestions.Question[].class));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
