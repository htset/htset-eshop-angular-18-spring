package com.example.Eshop.services;

import com.example.Eshop.dtos.ItemPayloadDTO;
import com.example.Eshop.exceptions.ItemNotFoundException;
import com.example.Eshop.models.Item;
import com.example.Eshop.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private ItemService itemService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetItems_Success() {
    int page = 1;
    int size = 10;
    String category = "Clothes";
    String name = "Nike sweater";

    List<Item> items = Arrays.asList(new Item(), new Item());
    Page<Item> itemPage = new PageImpl<>(items, PageRequest
        .of(page - 1, size), items.size());

    when(itemRepository
        .findByColumnContainingValuesAndFilter(anyList(),
            eq(name), any(PageRequest.class)))
        .thenReturn(itemPage);

    ItemPayloadDTO result = itemService.getItems(page, size, category, name);

    assertEquals(items.size(), result.getItems().size());
    assertEquals(items.size(), result.getCount());
  }

  @Test
  void testGetItems_InvalidPageParameters() {
    assertThrows(RuntimeException.class, () -> {
      itemService.getItems(0, 10, null, null);
    });
  }

  @Test
  void testGetItems_InternalServerError() {
    int page = 1;
    int size = 10;
    when(itemRepository.findAll(any(PageRequest.class)))
        .thenThrow(new RuntimeException("Unexpected error"));

    assertThrows(RuntimeException.class, () -> {
      itemService.getItems(page, size, null, null);
    });
  }

  @Test
  void testGetItemById_Success() {
    Long id = 1L;
    Item item = new Item();
    item.setId(id);
    when(itemRepository.findById(id))
        .thenReturn(Optional.of(item));

    Item result = itemService.getItemById(id);

    assertEquals(item, result);
  }

  @Test
  void testGetItemById_NotFound() {
    Long id = 1L;
    when(itemRepository.findById(id))
        .thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class, () -> {
      itemService.getItemById(id);
    });
  }

  @Test
  void testCreateItem_Success() {
    Item item = new Item();
    when(itemRepository.save(item)).thenReturn(item);

    Item result = itemService.createItem(item);

    assertEquals(item, result);
  }

  @Test
  void testCreateItem_InternalServerError() {
    Item item = new Item();
    when(itemRepository.save(item))
        .thenThrow(new RuntimeException("Unexpected error"));

    assertThrows(RuntimeException.class, () -> {
      itemService.createItem(item);
    });
  }

  @Test
  void testUpdateItem_Success() {
    Long id = 1L;
    Item existingItem = new Item();
    existingItem.setId(id);
    existingItem.setName("Old Name");

    Item updatedItem = new Item();
    updatedItem.setName("New Name");

    when(itemRepository.findById(id))
        .thenReturn(Optional.of(existingItem));
    when(itemRepository.save(existingItem))
        .thenReturn(existingItem);

    Item result = itemService.updateItem(id, updatedItem);

    assertEquals("New Name", result.getName());
  }

  @Test
  void testUpdateItem_NotFound() {
    Long id = 1L;
    Item updatedItem = new Item();
    when(itemRepository.findById(id))
        .thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class, () -> {
      itemService.updateItem(id, updatedItem);
    });
  }

  @Test
  void testDeleteItem_Success() {
    Long id = 1L;
    Item item = new Item();
    when(itemRepository.findById(id))
        .thenReturn(Optional.of(item));
    doNothing().when(itemRepository).delete(item);

    itemService.deleteItem(id);

    verify(itemRepository, times(1)).delete(item);
  }

  @Test
  void testDeleteItem_NotFound() {
    Long id = 1L;
    when(itemRepository.findById(id))
        .thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class, () -> {
      itemService.deleteItem(id);
    });
  }
}
