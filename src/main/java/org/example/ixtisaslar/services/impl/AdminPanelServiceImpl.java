package org.example.ixtisaslar.services.impl;

import org.example.ixtisaslar.dtos.AdminPanelDto;
import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;
import org.example.ixtisaslar.models.AdminPanelEntity;
import org.example.ixtisaslar.models.UserEntity;
import org.example.ixtisaslar.repositories.AdminPanelRepository;
import org.example.ixtisaslar.repositories.UserRepository;
import org.example.ixtisaslar.services.AdminPanelService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminPanelServiceImpl implements AdminPanelService {
    @Autowired
    private AdminPanelRepository adminPanelRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;


    @Override
    public AdminPanelDto getAdminById(Long adminId) {
        // Admin ID'sine göre admin kaydını bul
        Optional<AdminPanelEntity> adminOptional = adminPanelRepository.findById(adminId);

        // Eğer admin bulunursa DTO'ya çevir, yoksa null döndür
        return adminOptional.map(admin -> modelMapper.map(admin, AdminPanelDto.class)).orElse(null);
    }

    @Override
    public List<AdminPanelDto> getAllAdmins() {
        List<AdminPanelEntity> admins = adminPanelRepository.findAll(); // Tüm adminleri veritabanından al
        List<AdminPanelDto> result = admins.stream() // Admin listesi üzerinden akış oluştur
                .map(admin -> modelMapper.map(admin, AdminPanelDto.class)) // Her admini AdminPanelDto'ya dönüştür
                .collect(Collectors.toList()); // Sonucu listeye topla
        return result; // DTO listesini döndür
    }

    @Override
    public boolean login(LoginDto loginDto) {
        AdminPanelEntity admin = adminPanelRepository.findByUser_Email(loginDto.getEmail());
        if (admin == null) {
            return false; // Admin bulunamazsa false döner
        }

        // Şifre kontrolü
        boolean isPasswordMatch = bCryptPasswordEncoder.matches(loginDto.getPassword(), admin.getUser().getPassword());
        if (isPasswordMatch) {
            // Giriş başarılı ise last_login'ı güncelle
            admin.setLastLogin(new Timestamp(System.currentTimeMillis())); // Şu anki zamanı ayarla
            adminPanelRepository.save(admin); // Değişiklikleri kaydet
            return true; // Giriş başarılı
        }
        return false; // Giriş başarısız
    }

    @Override
    public boolean register(RegisterDto registerDto) {
        // Kullanıcının e-posta adresine göre var olup olmadığını kontrol et
        UserEntity existingUser = userRepository.findByEmail(registerDto.getEmail());
        if (existingUser == null) {
            System.out.println("E-posta veritabanında bulunamadı. Yeni kullanıcı oluşturulacak.");
        } else {
            System.out.println("E-posta zaten mevcut: " + existingUser.getEmail());
            return false;
        }

        // Yeni UserEntity oluştur
        UserEntity newUser = new UserEntity();
        newUser.setEmail(registerDto.getEmail());
        newUser.setFirstName(registerDto.getFirstname());
        newUser.setLastName(registerDto.getLastname());
        newUser.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword())); // Şifreyi hashle
        newUser.setCreatedAt(new Timestamp(System.currentTimeMillis())); // Kayıt oluşturulma tarihi
        newUser.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // Kayıt güncellenme tarihi

        // Yeni AdminPanelEntity oluştur ve kullanıcıyı admin paneli ile ilişkilendir
        AdminPanelEntity newAdmin = new AdminPanelEntity();
        newAdmin.setUser(newUser); // Admin ile kullanıcıyı ilişkilendir

        // Varsayılan izinleri JSON formatında ayarla
//        newAdmin.setPermission("{\"can_create\": true, \"can_read\": true, \"can_update\": true, \"can_delete\": false}"); // JSON formatında izinler
        newAdmin.setLastLogin(null); // İlk kayıt sırasında giriş zamanı null olabilir

        try {
            // Kullanıcıyı ve admini kaydet
            userRepository.save(newUser); // Kullanıcıyı önce kaydedin
            adminPanelRepository.save(newAdmin); // Sonra admin kaydını yapın
        } catch (Exception e) {
            e.printStackTrace(); // Hata loglama
            return false; // Kayıt işlemi başarısız
        }

        return true; // Başarıyla kayıt edildi
    }




}
