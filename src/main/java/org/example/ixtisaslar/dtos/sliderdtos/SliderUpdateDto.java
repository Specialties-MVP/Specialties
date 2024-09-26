package org.example.ixtisaslar.dtos.sliderdtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SliderUpdateDto {
    private MultipartFile photoFile;
}
