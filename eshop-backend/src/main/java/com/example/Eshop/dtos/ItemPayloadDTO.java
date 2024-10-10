package com.example.Eshop.dtos;

import com.example.Eshop.models.Item;
import lombok.Data;

import java.util.List;

@Data
public class ItemPayloadDTO {
  private List<Item> items;
  private long count;

  public ItemPayloadDTO(List<Item> items, long count) {
    this.items = items;
    this.count = count;
  }
}
