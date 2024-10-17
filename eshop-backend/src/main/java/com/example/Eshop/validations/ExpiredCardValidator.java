package com.example.Eshop.validations;

import com.example.Eshop.dtos.CreditCardDTO;
import com.example.Eshop.dtos.OrderDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExpiredCardValidator implements
    ConstraintValidator<NotExpired, OrderDTO> {

  @Override
  public boolean isValid(OrderDTO orderDTO, ConstraintValidatorContext context) {
    if (orderDTO == null || orderDTO.getCreditCard() == null)
      return false;

    CreditCardDTO creditCard = orderDTO.getCreditCard();
    return !creditCard.isCardExpired();
  }
}

