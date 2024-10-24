package org.example.ixtisaslar.services.impl;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.example.ixtisaslar.services.ChatGPTService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class ChatGPTServiceImpl implements ChatGPTService {
    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";



    @Override
    public String askQuestion(String question) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Yeni API URL'sini kullanıyoruz
        HttpPost request = new HttpPost("https://api.openai.com/v1/chat/completions");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + apiKey);

        // OpenAI'ye JSON formatında istek gönderiyoruz
        JsonObject json = new JsonObject();
        json.addProperty("model", "gpt-3.5-turbo");

        // Mesajlar formatı
        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", question);
        messages.add(userMessage);

        json.add("messages", messages);
        json.addProperty("max_tokens", 300);

        StringEntity entity = new StringEntity(json.toString(), StandardCharsets.UTF_8);
        request.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(request);
             BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))) {

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            // Yanıtı JSON formatında parse ediyoruz
            Gson gson = new Gson();
            JsonObject responseObject = gson.fromJson(result.toString(), JsonObject.class);

            // Burada yanıtı kontrol ediyoruz
            if (responseObject.has("choices")) {
                JsonArray choices = responseObject.getAsJsonArray("choices");
                if (choices != null && choices.size() > 0) {
                    String responseText = choices.get(0).getAsJsonObject().get("message").getAsJsonObject().get("content").getAsString();
                    return responseText;
                } else {
                    return "No choices found in response.";
                }
            } else {
                return "Invalid response format: 'choices' field not found.";
            }

        }
    }

}

