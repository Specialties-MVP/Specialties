package org.example.ixtisaslar.dtos.authdtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegisterDto {
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private String role; // Admin için ek role bilgisi
//    private String permissions;
}
