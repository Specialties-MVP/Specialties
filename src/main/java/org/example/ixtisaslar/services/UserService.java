package org.example.ixtisaslar.services;

import org.example.ixtisaslar.dtos.ResetPasswordDto;
import org.example.ixtisaslar.dtos.UserDto;
import org.example.ixtisaslar.dtos.authdtos.ForgetPasswordDto;
import org.example.ixtisaslar.dtos.authdtos.LoginDto;
import org.example.ixtisaslar.dtos.authdtos.RegisterDto;

import java.util.List;

public interface UserService {
//    boolean register(RegisterDto registerDto);


    //    boolean loginAsAdmin(LoginDto loginDto);
    boolean login(LoginDto loginDto);

    boolean createUser(RegisterDto registerDto);

    List<UserDto> getAllUsers();

    boolean deleteUser(Long userId);

    boolean forgetPassword(ForgetPasswordDto forgetPasswordDto);

    boolean resetPassword(ResetPasswordDto resetPasswordDto);

    boolean grantAdminRole(Long currentUserId, Long targetUserId);
}
