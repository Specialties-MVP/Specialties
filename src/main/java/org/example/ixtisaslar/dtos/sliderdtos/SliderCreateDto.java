package org.example.ixtisaslar.dtos.sliderdtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SliderCreateDto {
    private MultipartFile photoFile;

}
