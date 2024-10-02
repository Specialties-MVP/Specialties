package org.example.ixtisaslar.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String userType;
}
