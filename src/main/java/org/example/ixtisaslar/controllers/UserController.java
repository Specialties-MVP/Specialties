package org.example.ixtisaslar.controllers;

import org.example.ixtisaslar.dtos.UserDto;
import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;
import org.example.ixtisaslar.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Kullanıcı kaydı
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        try {
            boolean isRegistered = userService.createUser(registerDto);
            if (!isRegistered) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kayıt başarısız. Bu e-posta adresi zaten kayıtlı.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Kayıt başarılı.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Kayıt işlemi sırasında bir hata oluştu: " + e.getMessage());
        }
    }


    // Kullanıcı girişi
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        boolean isAuthenticated = userService.login(loginDto);
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Giriş başarısız. E-posta veya şifre hatalı.");
        }
        return ResponseEntity.ok("Giriş başarılı.");
    }

    // Tüm kullanıcıları listele
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users); // Kullanıcı DTO listesini döndür
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        boolean isDeleted = userService.deleteUser(userId);
        if (isDeleted) {
            return new ResponseEntity<>("Kullanıcı başarıyla silindi.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND);
        }
    }
}
