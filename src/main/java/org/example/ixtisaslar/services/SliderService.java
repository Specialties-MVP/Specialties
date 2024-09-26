package org.example.ixtisaslar.services;

import org.example.ixtisaslar.dtos.sliderdtos.SliderCreateDto;
import org.example.ixtisaslar.dtos.sliderdtos.SliderDto;
import org.example.ixtisaslar.dtos.sliderdtos.SliderUpdateDto;
import org.example.ixtisaslar.payloads.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SliderService  {
    ApiResponse create(SliderCreateDto sliderCreate);
    ApiResponse delete(Long id);
    ApiResponse update(SliderUpdateDto sliderUpdateDto, Long id);
    List<SliderDto> getAllSliders();

}
