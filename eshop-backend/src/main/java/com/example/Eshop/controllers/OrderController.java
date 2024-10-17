package com.example.Eshop.controllers;

import com.example.Eshop.dtos.OrderDTO;
import com.example.Eshop.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public ResponseEntity<OrderDTO> postOrder(@Valid @RequestBody OrderDTO dto) {
    try {
      OrderDTO createdOrder = orderService.createOrder(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}
