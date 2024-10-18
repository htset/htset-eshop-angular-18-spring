package com.example.Eshop.controllers;

import com.example.Eshop.dtos.ItemPayloadDTO;
import com.example.Eshop.models.Item;
import com.example.Eshop.services.ItemService;
import com.example.Eshop.exceptions.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ItemControllerTest {

  @Mock
  private ItemService itemService;

  @InjectMocks
  private ItemController itemController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetItems_Success() {
    int pageNumber = 1;
    int pageSize = 10;
    String category = "Clothes";
    String name = "Nike sweater";
    ItemPayloadDTO itemPayloadDTO = new ItemPayloadDTO();

    when(itemService.getItems(pageNumber, pageSize, category, name))
        .thenReturn(itemPayloadDTO);

    ResponseEntity<ItemPayloadDTO> response
        = itemController.getItems(pageNumber, pageSize, category, name);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(itemPayloadDTO, response.getBody());
  }

  @Test
  void testGetItems_InvalidPageParameters() {
    ResponseEntity<ItemPayloadDTO> response
        = itemController.getItems(0, 10, null, null);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(null, response.getBody());
  }

  @Test
  void testGetItems_InternalServerError() {
    int pageNumber = 1;
    int pageSize = 10;
    when(itemService.getItems(pageNumber, pageSize, null, null))
        .thenThrow(new RuntimeException("Unexpected error"));

    ResponseEntity<ItemPayloadDTO> response
        = itemController.getItems(pageNumber, pageSize, null, null);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(null, response.getBody());
  }

  @Test
  void testGetItemById_Success() {
    Long id = 1L;
    Item item = new Item();
    item.setId(id);
    when(itemService.getItemById(id)).thenReturn(item);

    ResponseEntity<Item> response = itemController.getItemById(id);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(item, response.getBody());
  }

  @Test
  void testGetItemById_NotFound() {
    Long id = 1L;
    when(itemService.getItemById(id))
        .thenThrow(new ItemNotFoundException("Item not found"));

    ResponseEntity<Item> response = itemController.getItemById(id);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(null, response.getBody());
  }

  @Test
  void testCreateItem_Success() {
    Item item = new Item();
    item.setName("New Item");
    Item createdItem = new Item();
    createdItem.setId(1L);
    createdItem.setName("New Item");

    when(itemService.createItem(item)).thenReturn(createdItem);

    ResponseEntity<Item> response = itemController.createItem(item);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(createdItem, response.getBody());
  }

  @Test
  void testCreateItem_InternalServerError() {
    Item item = new Item();
    when(itemService.createItem(item))
        .thenThrow(new RuntimeException("Unexpected error"));

    ResponseEntity<Item> response
        = itemController.createItem(item);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
        response.getStatusCode());
    assertEquals(null, response.getBody());
  }

  @Test
  void testUpdateItem_Success() {
    Long id = 1L;
    Item updatedItem = new Item();
    updatedItem.setName("Updated Item");
    Item returnedItem = new Item();
    returnedItem.setId(id);
    returnedItem.setName("Updated Item");

    when(itemService.updateItem(id, updatedItem))
        .thenReturn(returnedItem);

    ResponseEntity<Item> response
        = itemController.updateItem(id, updatedItem);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(returnedItem, response.getBody());
  }

  @Test
  void testUpdateItem_NotFound() {
    Long id = 1L;
    Item updatedItem = new Item();
    when(itemService.updateItem(id, updatedItem))
        .thenThrow(new ItemNotFoundException("Item not found"));

    ResponseEntity<Item> response = itemController.updateItem(id, updatedItem);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(null, response.getBody());
  }

  @Test
  void testDeleteItem_Success() {
    Long id = 1L;
    doNothing().when(itemService).deleteItem(id);

    ResponseEntity<Void> response
        = itemController.deleteItem(id);

    assertEquals(HttpStatus.NO_CONTENT,
        response.getStatusCode());
  }

  @Test
  void testDeleteItem_NotFound() {
    Long id = 1L;
    doThrow(new ItemNotFoundException("Item not found"))
        .when(itemService).deleteItem(id);

    ResponseEntity<Void> response
        = itemController.deleteItem(id);

    assertEquals(HttpStatus.NOT_FOUND,
        response.getStatusCode());
  }
}
