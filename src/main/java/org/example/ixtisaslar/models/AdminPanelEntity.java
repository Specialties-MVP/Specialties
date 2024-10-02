package org.example.ixtisaslar.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "admin_panel")
public class AdminPanelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

//    @Column(name = "permission", columnDefinition = "json") // JSON formatında izinler
//    private String permission; // Adminin izinlerini tutar

    @Column(name = "last_login") // Son giriş zamanını tutar
    private Timestamp lastLogin;
    @ManyToOne // Admin, bir kullanıcıya bağlıdır
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // Adminin hangi kullanıcıya ait olduğunu belirtir

}
