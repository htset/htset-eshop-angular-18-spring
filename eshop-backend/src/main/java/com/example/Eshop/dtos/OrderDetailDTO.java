package com.example.Eshop.dtos;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class OrderDetailDTO {

  private Long id;

  private Long orderId;

  @NotNull
  private Long itemId;

  private String itemName;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  // Quantity must be greater than 0
  private BigDecimal quantity;

  private BigDecimal itemUnitPrice;

  private BigDecimal totalPrice;
}
