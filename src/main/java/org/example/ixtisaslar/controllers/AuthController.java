package org.example.ixtisaslar.controllers;

import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;
import org.example.ixtisaslar.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        boolean res = userService.login(loginDto);
        if (!res) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
        return ResponseEntity.ok("Login successful");
    }

    // Admin girişi
    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin(@RequestBody LoginDto loginDto) {
        boolean isAdmin = userService.loginAsAdmin(loginDto);

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin login failed. You are not authorized.");
        }

        return ResponseEntity.ok("Admin login successful");
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        boolean res = userService.register(registerDto);
        if (!res) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
        }
        return ResponseEntity.ok("Registration successful");
    }
}
