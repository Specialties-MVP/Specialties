package org.example.ixtisaslar.services.impl;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.ixtisaslar.services.ChatGPTService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class ChatGPTServiceImpl implements ChatGPTService {

    private static final Dotenv dotenv = Dotenv.configure()
            .directory(System.getProperty("user.dir"))  // Proje kök dizini
            .load();
    private static final String apiKey = dotenv.get("OPENAI_API_KEY");

    private String pdfContent = "";
    private boolean isPdfAnalyzed = false;  // PDF'in analiz edilip edilmediğini kontrol eden bayrak

    @Override
    public String askQuestion(String question) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // API anahtarını kontrol edin
        System.out.println("API Key: " + apiKey);

        // Yeni API URL'sini kullanıyoruz
        HttpPost request = new HttpPost("https://api.openai.com/v1/chat/completions");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + apiKey);

        // Eğer PDF içerik varsa soruya dahil ediyoruz ve kullanıcıya bildiriyoruz
        String content;
        if (isPdfAnalyzed) {
            content = "AI'nin analiz ettiği PDF içeriği ile beraber cevap veriliyor.\n\n" + pdfContent + "\n\nSoru: " + question;

            // Burada gönderilen soruyu ve PDF içeriğini kontrol edin
            System.out.println("AI'ye gönderilen içerik (soru + PDF): " + content);
        } else {
            content = question;
            System.out.println("AI'ye gönderilen soru: " + content);
        }



        // OpenAI'ye JSON formatında istek gönderiyoruz
        JsonObject json = new JsonObject();
        json.addProperty("model", "gpt-3.5-turbo");

        // Mesajlar formatı
        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", content);
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
            // İşte burada API'den gelen ham yanıtı yazdırıyorsunuz
            System.out.println("Ham API Yanıtı: " + result.toString());

            // Yanıtı JSON formatında parse ediyoruz
            Gson gson = new Gson();
            JsonObject responseObject = gson.fromJson(result.toString(), JsonObject.class);

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

    @Override
    public String analyzePdf(MultipartFile file) throws Exception {
        // PDF dosyasını text formatına çevir
        String content = extractTextFromPdf(file);

        if (content.isEmpty()) {
            this.isPdfAnalyzed = false;  // PDF boşsa bayrağı false yap
            return "Error: PDF dosyası okunamadı.";
        }

        this.pdfContent = content;
        this.isPdfAnalyzed = true;  // PDF analiz edildi

        // Buraya PDF içeriğini yazdırın
        System.out.println("PDF İçeriği: " + pdfContent);

        return "PDF analizi başarılı: Dosya içeriği sorulara eklendi.";
    }


    // PDF dosyasını text formatına çevirme
    private String extractTextFromPdf(MultipartFile file) throws Exception {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }
}
