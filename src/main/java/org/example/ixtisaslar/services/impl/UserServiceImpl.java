package org.example.ixtisaslar.services.impl;

import org.example.ixtisaslar.dtos.ResetPasswordDto;
import org.example.ixtisaslar.dtos.UserDto;
import org.example.ixtisaslar.dtos.authdtos.ForgetPasswordDto;
import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;
import org.example.ixtisaslar.models.User;
import org.example.ixtisaslar.repositories.UserRepository;
import org.example.ixtisaslar.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.example.ixtisaslar.models.UserType;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JavaMailSender mailSender;


    // İstifadəçi girişi
    @Override
    public boolean login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail());
        if (user == null) {
            return false; // İstifadəçi tapılmadısa false qaytarır
        }

        // Şifrəni yoxlayır
        if (bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            // Əgər giriş uğurludursa, lastLogin tarixini yenilə
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user); // Yenilənmiş istifadəçini yadda saxlayır

            return true; // Uğurla giriş edildi
        }

        return false; // Şifrə yalnışdırsa false qaytarır
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordPattern);
    }

    // Yeni istifadəçi yaratma
    @Override
    public boolean createUser(RegisterDto registerDto) {
        User existingUser = userRepository.findByEmail(registerDto.getEmail());
        if (existingUser != null) {
            return false; // Əgər istifadəçi artıq mövcuddursa false qaytarır
        }

        // Şifrənin keçərliliyini yoxlayır
        if (!isValidPassword(registerDto.getPassword())) {
            throw new IllegalArgumentException("Şifrə ən azı 8 simvol olmalı və bir böyük hərf, bir kiçik hərf, bir rəqəm və bir xüsusi simvol olmalıdır.");
        }

        registerDto.setUserType("MENTOR");
        // Əgər e-poçt məlumat bazasında yoxdursa, yeni istifadəçi qeydiyyatına davam edir
        User newUser = modelMapper.map(registerDto, User.class);
        newUser.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword())); // Şifrəni hash edir

        // Yeni istifadəçini məlumat bazasına yadda saxlayır
        userRepository.save(newUser);
        return true; // Uğurla qeydiyyatdan keçdi
    }


    // Bütün istifadəçiləri siyahıya alır
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    // İstifadəçi silmə
    @Override
    public boolean deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    // "Şifrəmi unutdum" prosesi
    @Override
    public boolean forgetPassword(ForgetPasswordDto forgetPasswordDto) {
        // İstifadəçinin e-poçt ünvanı ilə məlumat bazasında axtarış edilir
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(forgetPasswordDto.getEmail()));

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // İstifadəçiyə xüsusi təsdiq kodu yaradılır
            String verificationCode = generateVerificationCode();

            // İstifadəçinin profilində təsdiq kodunu saxlamaq istəyirsinizsə, buraya əlavə edə bilərsiniz
            // user.setVerificationCode(verificationCode);
            // userRepository.save(user);

            // İstifadəçinin e-poçt ünvanına təsdiq kodunu göndərir
            sendVerificationEmail(user.getEmail(), verificationCode);
            user.setVerificationCode(verificationCode);
            userRepository.save(user);
            return true;
        }
        return false; // İstifadəçi tapılmadısa
    }

    private String generateVerificationCode() {
        // 6 rəqəmli təsdiq kodu yaradılır
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ilə 999999 arasında bir rəqəm
        return String.valueOf(code);
    }

    private void sendVerificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail); // İstifadəçinin e-poçt ünvanı
        message.setSubject("Şifrə sıfırlama kodu");
        message.setText("Şifrənizi sıfırlamaq üçün təsdiq kodunuz: " + verificationCode);
        mailSender.send(message); // E-poçt göndərmə prosesi
    }

    @Override
    public boolean resetPassword(ResetPasswordDto resetPasswordDto) {
        // İstifadəçini e-poçt ünvanı ilə tapır
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(resetPasswordDto.getEmail()));

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Təsdiq kodunu yoxlayır
            if (user.getVerificationCode() != null && user.getVerificationCode().equals(resetPasswordDto.getVerificationCode())) {
                // Yeni şifrəni hash edib təyin edir
                user.setPassword(bCryptPasswordEncoder.encode(resetPasswordDto.getNewPassword()));
                user.setVerificationCode(null); // Təsdiq kodunu sıfırlayır

                userRepository.save(user); // Yenilənmiş istifadəçini yadda saxlayır
                return true;
            }
        }
        return false; // E-poçt və ya təsdiq kodu yalnışdırsa
    }

    @Override
    public boolean grantAdminRole(Long currentUserId, Long targetUserId) {
        // Əməliyyatı icra edən istifadəçini tapır
        Optional<User> currentUserOptional = userRepository.findById(currentUserId);
        if (!currentUserOptional.isPresent()) {
            throw new IllegalArgumentException("Əməliyyatı icra edən istifadəçi tapılmadı.");
        }

        User currentUser = currentUserOptional.get();

        // Əməliyyatı icra edən istifadəçinin ADMIN olub-olmadığını yoxlayır
        if (!currentUser.getUserType().equals(UserType.ADMIN)) {
            throw new SecurityException("Bu əməliyyatı icra etmək üçün yetkiniz yoxdur.");
        }

        // Hədəf istifadəçini tapır
        Optional<User> targetUserOptional = userRepository.findById(targetUserId);
        if (!targetUserOptional.isPresent()) {
            throw new IllegalArgumentException("Hədəf istifadəçi tapılmadı.");
        }

        User targetUser = targetUserOptional.get();

        // Hədəf istifadəçinin rolunu ADMIN olaraq dəyişdirir
        targetUser.setUserType(UserType.ADMIN);
        userRepository.save(targetUser); // Dəyişiklikləri yadda saxlayır

        return true; // Əməliyyat uğurla tamamlandı
    }
}
