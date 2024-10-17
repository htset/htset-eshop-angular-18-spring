package com.example.Eshop.repositories;

import com.example.Eshop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);
  User findByEmail(String email);
  User findByRegistrationCode(String registrationCode);
}
