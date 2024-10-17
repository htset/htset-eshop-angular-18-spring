package com.example.Eshop.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditCardDTO{

  @NotNull
  @Pattern(regexp = "^[0-9]{16}$", message = "Card number consists of 16 numbers")
  private String cardNumber;

  @NotNull
  private String holderName;

  @NotNull
  @Pattern(regexp = "^[0-9]{3}$", message = "CVV consists of 3 numbers")
  private String code;

  @NotNull
  @Min(1)
  @Max(12)
  private Integer expiryMonth;

  @NotNull
  private Integer expiryYear;

  //Validate if the card is expired
  public boolean isCardExpired() {
    LocalDate currentDate = LocalDate.now();
    LocalDate cardExpiryDate = LocalDate.of(expiryYear,
        expiryMonth, 1).withDayOfMonth(1);
    return currentDate.isAfter(cardExpiryDate);
  }
}