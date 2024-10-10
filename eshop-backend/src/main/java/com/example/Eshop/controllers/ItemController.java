package com.example.Eshop.controllers;

import com.example.Eshop.dtos.ItemPayloadDTO;
import com.example.Eshop.models.Item;
import com.example.Eshop.services.ItemService;
import com.example.Eshop.exceptions.ItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/items")
public class ItemController {

  private ItemService itemService;
  private final Logger logger = LoggerFactory.getLogger(ItemController.class);

  public ItemController(ItemService itemService){
    this.itemService = itemService;
  }

  @GetMapping
  public ResponseEntity<ItemPayloadDTO> getItems() {
    try {
      ItemPayloadDTO itemPayload = itemService.getItems();
      return ResponseEntity.ok(itemPayload); //Return 200 OK with the item payload
    } catch (Exception e) {
      logger.error("Error fetching items: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null); //Return 500 Internal Server Error
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Item> getItemById(@PathVariable Long id) {
    try {
      Item item = itemService.getItemById(id);
      return ResponseEntity.ok(item); //Return 200 OK with the item
    } catch (ItemNotFoundException e) {
      logger.error("Item not found with id {}: {}", id, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(null); //Return 404 Not Found if the item is not found
    } catch (Exception e) {
      logger.error("Error fetching item by id {}: {}", id, e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null); //Return 500 Internal Server Error for other issues
    }
  }
}
