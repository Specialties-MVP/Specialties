package org.example.ixtisaslar.repositories;

import org.example.ixtisaslar.models.AdminPanelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminPanelRepository extends JpaRepository<AdminPanelEntity,Long> {
    AdminPanelEntity findByUser_Email(String email);
}
