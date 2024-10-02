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

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
//        boolean res = userService.login(loginDto);
//        if (!res) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
//        }
//        return ResponseEntity.ok("Login successful");
//    }

}
