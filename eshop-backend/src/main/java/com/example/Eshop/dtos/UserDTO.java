package com.example.Eshop.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  private Long id;
  private String username;
  private String status;
  private String role;
  private String token;
  private String refreshToken;
  private Date refreshTokenExpiry;
}