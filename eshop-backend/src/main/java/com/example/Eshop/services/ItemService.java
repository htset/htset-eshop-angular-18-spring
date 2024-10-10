package com.example.Eshop.services;

import com.example.Eshop.dtos.ItemPayloadDTO;
import com.example.Eshop.exceptions.ItemNotFoundException;
import com.example.Eshop.models.Item;
import com.example.Eshop.repositories.ItemRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ItemService {

  private final ItemRepository itemRepository;
  private static final Logger logger
      = LoggerFactory.getLogger(ItemService.class);

  public ItemService(ItemRepository itemRepository){
    this.itemRepository = itemRepository;
  }

  //Get all items
  public ItemPayloadDTO getItems() {
    try{
      List<Item> items = itemRepository.findAll();
      return new ItemPayloadDTO(items, items.size());
    } catch (Exception e) {
      logger.error("Error fetching items: {}", e.getMessage());
      throw new RuntimeException("Unable to fetch items, please try again later");
    }
  }

  //Get one item by ID
  public Item getItemById(Long id) {
    try {
      return itemRepository.findById(id)
          .orElseThrow(()
              -> new ItemNotFoundException("Item not found with id: " + id));
    } catch (ItemNotFoundException e) {
      logger.error("Error fetching item by id {}: {}", id, e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error("Error fetching item by id {}: {}", id, e.getMessage());
      throw new RuntimeException("Unable to fetch item, please try again later");
    }
  }
}
