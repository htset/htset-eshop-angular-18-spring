package com.example.Eshop.dtos;

import lombok.Data;
@Data
public class LogMessageDTO {
  private String message;
  private String level;
  private String stackTrace;
}
