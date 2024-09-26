package org.example.ixtisaslar.services.impl;

import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;
import org.example.ixtisaslar.models.UserEntity;
import org.example.ixtisaslar.repositories.UserRepository;
import org.example.ixtisaslar.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public boolean register(RegisterDto registerDto) {
        UserEntity user = userRepository.findByEmail(registerDto.getEmail());
        if (user != null) {
            return false;
        }
        String hashPassword = bCryptPasswordEncoder.encode(registerDto.getPassword());
        Random random = new Random();
        String token = bCryptPasswordEncoder.encode(registerDto.getEmail());
        UserEntity newUser = modelMapper.map(registerDto, UserEntity.class);
        newUser.setAdmin(false);
        newUser.setConfirmationToken(token);
        newUser.setPassword(hashPassword);
        userRepository.save(newUser);
        return true;
    }

    @Override
    public boolean login(LoginDto loginDto) {
        UserEntity user = userRepository.findByEmail(loginDto.getEmail());


        if (user == null) {
            return false;
        }

        if (bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword()) && !user.isAdmin()) {
            return true;
        }


        return false;
    }

    // Sadece admin girişi
    public boolean loginAsAdmin(LoginDto loginDto) {
        UserEntity user = userRepository.findByEmail(loginDto.getEmail());

        if (user == null || !bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return false;
        }

        if (!user.isAdmin()) {
            return false;
        }

        return true;
    }

}
