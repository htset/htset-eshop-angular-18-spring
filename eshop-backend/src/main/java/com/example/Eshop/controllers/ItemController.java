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
  public ResponseEntity<ItemPayloadDTO> getItems(
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String name) {
    try {
      //Validate pageNumber and pageSize
      if (pageNumber < 1 || pageSize < 1) {
        return ResponseEntity.badRequest()
            .body(null); //Return 400 Bad Request for invalid page parameters
      }

      ItemPayloadDTO itemPayload = itemService
          .getItems(pageNumber, pageSize, category, name);
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

  @PostMapping
  public ResponseEntity<Item> createItem(@RequestBody Item item) {
    try {
      Item createdItem = itemService.createItem(item);
      //Return 201 Created with the new item
      return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }
    catch (Exception e) {
      logger.error("Error creating item: {}", e.getMessage());
      //Return 500 Internal Server Error if item creation fails
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Item> updateItem(@PathVariable Long id,
                                         @RequestBody Item updatedItem) {
    try {
      Item item = itemService.updateItem(id, updatedItem);
      //Return 200 OK with the updated item
      return ResponseEntity.ok(item);
    }
    catch (ItemNotFoundException e) {
      logger.error("Error updating item with id {}: {}", id, e.getMessage());
      //Return 404 Not Found if the item to be updated doesn't exist
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(null);
    }
    catch (Exception e) {
      logger.error("Error updating item with id {}: {}", id, e.getMessage());
      //Return 500 Internal Server Error for other issues
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
    try {
      itemService.deleteItem(id);
      //Return 204 No Content for successful deletion
      return ResponseEntity.noContent().build();
    } catch (ItemNotFoundException e) {
      logger.error("Error deleting item with id {}: {}", id, e.getMessage());
      //Return 404 Not Found if the item doesn't exist
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      logger.error("Error deleting item with id {}: {}", id, e.getMessage());
      //Return 500 Internal Server Error for other issues
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
