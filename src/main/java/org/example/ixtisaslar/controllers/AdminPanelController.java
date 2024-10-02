package org.example.ixtisaslar.controllers;


import org.example.ixtisaslar.dtos.AdminPanelDto;
import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;
import org.example.ixtisaslar.services.AdminPanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminPanelController {
    @Autowired
    private AdminPanelService adminPanelService;

    // Tüm adminleri listele
    @GetMapping
    public ResponseEntity<List<AdminPanelDto>> getAllAdmins() {
        List<AdminPanelDto> admins = adminPanelService.getAllAdmins();
        return ResponseEntity.ok(admins); // Admin DTO listesini döndür
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<AdminPanelDto> getAdminById(@PathVariable Long adminId) {
        AdminPanelDto admin = adminPanelService.getAdminById(adminId);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Eğer admin bulunamazsa 404 döner
        }
        return ResponseEntity.ok(admin); // Bulunan admini döndür
    }

    // Admin girişi
    @PostMapping("/login")
    public ResponseEntity<String> adminLogin(@RequestBody LoginDto loginDto) {
        boolean isAuthenticated = adminPanelService.login(loginDto);
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin girişi başarısız. E-posta veya şifre hatalı.");
        }
        return ResponseEntity.ok("Admin girişi başarılı.");
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterDto registerDto) {
        try {
            boolean isRegistered = adminPanelService.register(registerDto);
            if (!isRegistered) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kayıt başarısız. Bu e-posta adresi zaten kayıtlı.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Admin başarıyla kaydedildi.");
        } catch (Exception e) {
            // Hata durumunda 500 Internal Server Error döner
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Admin kaydı sırasında bir hata oluştu: " + e.getMessage());
        }
    }


//    @PostMapping("/register")
//    public ResponseEntity<String> registerAdmin(@RequestBody RegisterDto registerDto) {
//        boolean isRegistered = adminPanelService.register(registerDto); // Kullanıcı hizmetinden admin kaydı
//        if (!isRegistered) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kayıt başarısız. Bu e-posta adresi zaten kayıtlı.");
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).body("Admin başarıyla kaydedildi.");
//    }
}
