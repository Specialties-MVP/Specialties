package org.example.ixtisaslar.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPanelDto {
    private Long adminId; // Admin ID
    private Long userId; // Kullanıcı ID
//    private String permissions; // Admin izinleri
    private String lastLogin; // Son giriş tarihi
}