package org.example.ixtisaslar.repositories;

import org.example.ixtisaslar.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

}
