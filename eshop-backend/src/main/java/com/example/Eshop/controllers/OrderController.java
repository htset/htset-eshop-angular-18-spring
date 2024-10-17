package com.example.Eshop.controllers;

import com.example.Eshop.dtos.OrderDTO;
import com.example.Eshop.dtos.OrderPayloadDTO;
import com.example.Eshop.models.Order;
import com.example.Eshop.services.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

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

  @QueryMapping
  public OrderPayloadDTO getOrders(@Argument int page,
                                   @Argument int pageSize,
                                   @Argument String search) {
    List<Order> orders
        = orderService.findPaginatedOrders(page -1, pageSize, search);
    int totalCount = orderService.getTotalOrderCount(search);

    OrderPayloadDTO response = new OrderPayloadDTO();
    response.setOrders(orders);
    response.setTotalCount(totalCount);

    return response;
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
    Optional<OrderDTO> orderDTO = orderService.getOrderById(id);
    return orderDTO.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
