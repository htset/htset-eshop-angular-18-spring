package com.example.Eshop.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String email;
  private String status;
  private String role;
  private String token;
  private String refreshToken;
  private Date refreshTokenExpiry;
  private String registrationCode;
}