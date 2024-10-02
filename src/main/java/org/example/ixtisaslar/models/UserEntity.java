package org.example.ixtisaslar.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // user_id ile eşleşecek şekilde değiştirdik
    @Column(name = "first_name") // Veritabanındaki field adıyla eşleşiyor
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    private String password;
    @Column(name = "user_type")
    private String userType; // user_type eklendi
    private Timestamp createdAt;
    private Timestamp updatedAt;


}
