package org.example.ixtisaslar.controllers;

import org.example.ixtisaslar.dtos.ResetPasswordDto;
import org.example.ixtisaslar.dtos.UserDto;
import org.example.ixtisaslar.dtos.authdtos.ForgetPasswordDto;
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

    // İstifadəçi qeydiyyatı
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        try {
            boolean isRegistered = userService.createUser(registerDto);
            if (!isRegistered) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Qeydiyyat uğursuz oldu. Bu e-poçt ünvanı artıq mövcuddur.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Qeydiyyat uğurla tamamlandı.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Qeydiyyat zamanı bir xəta baş verdi: " + e.getMessage());
        }
    }

    // İstifadəçi girişi
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        boolean isAuthenticated = userService.login(loginDto);
        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Giriş uğursuz oldu. E-poçt və ya şifrə yalnışdır.");
        }
        return ResponseEntity.ok("Giriş uğurla tamamlandı.");
    }

    // Bütün istifadəçiləri siyahıya alır
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // İstifadəçi silmə
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        boolean isDeleted = userService.deleteUser(userId);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK).body("İstifadəçi uğurla silindi.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("İstifadəçi tapılmadı.");
        }
    }

    // "Şifrəmi unutdum" prosesi
    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPasswordDto forgetPasswordDto) {
        boolean result = userService.forgetPassword(forgetPasswordDto);
        if (result) {
            return ResponseEntity.ok("Şifrə sıfırlama e-poçtu göndərildi.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("İstifadəçi tapılmadı.");
    }

    // Şifrə sıfırlama prosesi
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        boolean isReset = userService.resetPassword(resetPasswordDto);

        if (isReset) {
            return ResponseEntity.ok("Şifrə uğurla sıfırlandı.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-poçt və ya təsdiq kodu yalnışdır.");
        }
    }

    // Admin yetkisi vermə
    @PostMapping("/grant-admin/{currentUserId}/{targetUserId}")
    public ResponseEntity<String> grantAdminRole(@PathVariable Long currentUserId, @PathVariable Long targetUserId) {
        try {
            boolean isGranted = userService.grantAdminRole(currentUserId, targetUserId);
            if (isGranted) {
                return ResponseEntity.ok("İstifadəçiyə admin yetkisi uğurla verildi.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admin yetkisi verilə bilmədi.");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu əməliyyatı icra etmək üçün yetkiniz yoxdur.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Bir xəta baş verdi: " + e.getMessage());
        }
    }
}
