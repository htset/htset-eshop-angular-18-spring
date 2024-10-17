package com.example.Eshop.dtos;
import com.example.Eshop.models.Order;
import lombok.Data;

import java.util.List;

@Data
public class OrderPayloadDTO {
  private int totalCount; //Total number of orders matching the criteria
  private List<Order> orders; //List of paginated orders
}
