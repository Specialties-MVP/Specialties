package org.example.ixtisaslar.services;

import org.example.ixtisaslar.dtos.AdminPanelDto;
import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;

import java.util.List;

public interface AdminPanelService {
    AdminPanelDto getAdminById(Long adminId);  // Admin ID'ye göre admin bilgilerini getirme
    List<AdminPanelDto> getAllAdmins();
    boolean login(LoginDto loginDto);
    boolean register(RegisterDto registerDto);
}

