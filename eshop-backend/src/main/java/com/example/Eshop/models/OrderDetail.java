package com.example.Eshop.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
public class OrderDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @NotNull
  @Column(nullable = false)
  private Long itemId;

  @NotBlank
  @Column(nullable = false)
  private String itemName;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal itemUnitPrice;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal quantity;

  @NotNull
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice;

  //Custom validation for Quantity > 0
  @AssertTrue(message = "Quantity must be greater than 0")
  public boolean isValidQuantity() {
    return quantity.compareTo(BigDecimal.ZERO) > 0;
  }

  @Override
  public String toString() {
    return "OrderDetail{" +
        "id=" + id +
        ", itemId=" + itemId +
        ", itemName='" + itemName + '\'' +
        ", itemUnitPrice=" + itemUnitPrice +
        ", quantity=" + quantity +
        ", totalPrice=" + totalPrice +
        '}';
  }
}