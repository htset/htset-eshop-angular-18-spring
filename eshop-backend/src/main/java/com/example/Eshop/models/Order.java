package com.example.Eshop.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incremented ID
  @Column(nullable = false)
  private Long id;

  @NotNull
  @Column(nullable = false)
  private Long userId;

  @NotNull
  @Column(nullable = false)
  private LocalDateTime orderDate;

  @NotNull
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice;

  @NotNull
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderDetail> orderDetails;

  @NotBlank
  @Column(nullable = false)
  private String firstName;

  @NotBlank
  @Column(nullable = false)
  private String lastName;

  @NotBlank
  @Column(nullable = false)
  private String street;

  @NotBlank
  @Column(nullable = false)
  private String zip;

  @NotBlank
  @Column(nullable = false)
  private String city;

  @NotBlank
  @Column(nullable = false)
  private String country;


  public Order(){
    orderDetails = new ArrayList<OrderDetail>();
  }

  @Override
  public String toString() {
    return "Order{" +
        "id=" + id +
        ", userId=" + userId +
        ", orderDate=" + orderDate +
        ", totalPrice=" + totalPrice +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", street='" + street + '\'' +
        ", zip='" + zip + '\'' +
        ", city='" + city + '\'' +
        ", country='" + country + '\'' +
        '}';
  }
}