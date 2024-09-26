package org.example.ixtisaslar.services;

import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;

public interface UserService {
    boolean register(RegisterDto registerDto);

    boolean login(LoginDto loginDto);

    boolean loginAsAdmin(LoginDto loginDto);
}
