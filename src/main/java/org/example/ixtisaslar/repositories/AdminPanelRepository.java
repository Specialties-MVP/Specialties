package org.example.ixtisaslar.repositories;

import org.example.ixtisaslar.models.AdminPanel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminPanelRepository extends JpaRepository<AdminPanel,Long> {
    AdminPanel findByUser_Email(String email);
}
