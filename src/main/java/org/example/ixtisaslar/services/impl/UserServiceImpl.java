package org.example.ixtisaslar.services.impl;

import org.example.ixtisaslar.dtos.UserDto;
import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;
import org.example.ixtisaslar.models.UserEntity;
import org.example.ixtisaslar.repositories.UserRepository;
import org.example.ixtisaslar.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    @Override
    public boolean login(LoginDto loginDto) {
        UserEntity user = userRepository.findByEmail(loginDto.getEmail());
        if (user == null) {
            return false; // Kullanıcı bulunamazsa false döner
        }

        return bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword()); // Şifreyi kontrol et
    }


    // Sadece admin girişi
//    public boolean loginAsAdmin(LoginDto loginDto) {
//        UserEntity user = userRepository.findByEmail(loginDto.getEmail());
//
//        if (user == null || !bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
//            return false;
//        }
//
//        if (!user.isAdmin()) {
//            return false;
//        }
//
//        return true;
//    }

    @Override
    public boolean createUser(RegisterDto registerDto) {
        UserEntity existingUser = userRepository.findByEmail(registerDto.getEmail());
        if (existingUser != null) {
            return false; // Eğer kullanıcı zaten varsa false döner
        }

        // Eğer e-posta veritabanında yoksa yeni kullanıcı kaydına devam et
        UserEntity newUser = modelMapper.map(registerDto, UserEntity.class);
        newUser.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword())); // Şifreyi hashle
        newUser.setCreatedAt(new Timestamp(System.currentTimeMillis())); // Kayıt oluşturulma tarihi
        newUser.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // Kayıt güncellenme tarihi

        // Yeni kullanıcıyı veritabanına kaydet
        userRepository.save(newUser);
        return true; // Başarıyla kayıt edildi
    }


    @Override
    public List<UserDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll(); // Tüm kullanıcıları veritabanından al
        List<UserDto> result = users.stream() // Kullanıcı listesi üzerinden akış oluştur
                .map(user -> modelMapper.map(user, UserDto.class)) // Her kullanıcıyı UserDto'ya dönüştür
                .collect(Collectors.toList()); // Sonucu listeye topla
        return result; // DTO listesini döndür
    }

    @Override
    public boolean deleteUser(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userRepository.deleteById(userId); // Kullanıcıyı sil
            return true; // Silme işlemi başarılı
        }
        return false; // Kullanıcı bulunamadıysa silme işlemi başarısız
    }

}