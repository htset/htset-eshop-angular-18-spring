package com.example.Eshop.dtos;

import com.example.Eshop.validations.NotExpired;
import jakarta.validation.Valid;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NotExpired //Use custom annotation
public class OrderDTO {

  private Long id;

  @NotNull
  private Long userId;

  private LocalDateTime orderDate;

  private BigDecimal totalPrice;

  @NotNull
  private List<OrderDetailDTO> orderDetails;

  @Valid
  private CreditCardDTO creditCard;

  @NotNull
  private Long deliveryAddressId;

  public OrderDTO(){
    orderDetails = new ArrayList<OrderDetailDTO>();
  }
}
