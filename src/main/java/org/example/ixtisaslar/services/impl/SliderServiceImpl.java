package org.example.ixtisaslar.services.impl;

import org.example.ixtisaslar.dtos.sliderdtos.SliderCreateDto;
import org.example.ixtisaslar.dtos.sliderdtos.SliderDto;
import org.example.ixtisaslar.dtos.sliderdtos.SliderUpdateDto;
import org.example.ixtisaslar.models.Slider;
import org.example.ixtisaslar.payloads.ApiResponse;
import org.example.ixtisaslar.repositories.SliderRepository;
import org.example.ixtisaslar.services.SliderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SliderServiceImpl implements SliderService {
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ApiResponse create(SliderCreateDto sliderCreate) {
        Slider slider = modelMapper.map(sliderCreate, Slider.class);
        MultipartFile file = sliderCreate.getPhotoFile();

        if (file != null && !file.isEmpty()) {
            try {
                // Dosyanın orijinal adını alıp uzantısını ayırıyoruz
                String originalFileName = file.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

                // Benzersiz bir UUID oluşturuyoruz
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                // Yükleme dizinini kontrol edip oluşturuyoruz (eğer yoksa)
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Dosyayı belirtilen dizine kaydediyoruz
                Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);
                Files.write(filePath, file.getBytes());

                // Slider nesnesine dosya yolunu kaydediyoruz
                slider.setPhotoUrl(uniqueFileName);

            } catch (IOException e) {
                return new ApiResponse(false, "Fotoğraf yüklenirken hata oluştu: " + e.getMessage());
            }
        } else {
            return new ApiResponse(false, "Fotoğraf dosyası boş olamaz!");
        }

        sliderRepository.save(slider);
        return new ApiResponse(true, "Slider başarıyla oluşturuldu!");
    }

    @Override
    public ApiResponse delete(Long id) {
        Slider slider = sliderRepository.findById(id).orElseThrow(() -> new RuntimeException("Slider bulunamadı"));

        if (slider.getPhotoUrl() != null && !slider.getPhotoUrl().isEmpty()) {
            Path filePath = Paths.get(UPLOAD_DIR + slider.getPhotoUrl());
            try {
                boolean isDeleted = Files.deleteIfExists(filePath);
                if (isDeleted) {
                    System.out.println("Fotoğraf başarıyla silindi: " + filePath.toAbsolutePath());
                } else {
                    System.out.println("Fotoğraf bulunamadı ya da silinemedi: " + filePath.toAbsolutePath());
                }
            } catch (IOException e) {
                System.out.println("Fotoğraf silinirken hata oluştu: " + e.getMessage());
                return new ApiResponse(false, "Fotoğraf silinirken hata oluştu: " + e.getMessage());
            }
        }

        sliderRepository.delete(slider);
        return new ApiResponse(true, "Slider başarıyla silindi!");
    }

    @Override
    public ApiResponse update(SliderUpdateDto sliderUpdateDto, Long id) {
       Slider slider = sliderRepository.findById(id).orElseThrow();

        // Fotoğraf dosyası güncelleniyorsa
        MultipartFile file = sliderUpdateDto.getPhotoFile();
        if (file != null && !file.isEmpty()) {
            // Eski fotoğrafı siliyoruz (eğer varsa)
            if (slider.getPhotoUrl() != null && !slider.getPhotoUrl().isEmpty()) {
                Path oldFilePath = Paths.get(UPLOAD_DIR + slider.getPhotoUrl());
                try {
                    Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {
                    return new ApiResponse(false, "Eski dosya silinirken hata oluştu: " + e.getMessage());
                }
            }

            // Yeni dosyayı kaydediyoruz
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            Path newFilePath = Paths.get(UPLOAD_DIR + uniqueFileName);
            try {
                Files.write(newFilePath, file.getBytes());
                slider.setPhotoUrl(uniqueFileName);  // Yeni dosya yolunu kaydediyoruz
            } catch (IOException e) {
                return new ApiResponse(false, "Yeni dosya kaydedilirken hata oluştu: " + e.getMessage());
            }
        }
        // Veritabanında slider kaydını güncelliyoruz
        sliderRepository.save(slider);

        return new ApiResponse(true, "Slider başarıyla güncellendi!");
    }

    @Override
    public List<SliderDto> getAllSliders() {
        List<Slider> sliders = sliderRepository.findAll();
        List<SliderDto> result = sliders.stream().map(slider -> modelMapper.map(slider,SliderDto.class)).collect(Collectors.toList());
        return result;
    }

}
