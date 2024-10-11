package com.example.Eshop.services;

import com.example.Eshop.dtos.ItemPayloadDTO;
import com.example.Eshop.exceptions.ItemNotFoundException;
import com.example.Eshop.models.Item;
import com.example.Eshop.repositories.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Service
public class ItemService {

  private final ItemRepository itemRepository;
  private static final Logger logger
      = LoggerFactory.getLogger(ItemService.class);

  public ItemService(ItemRepository itemRepository){
    this.itemRepository = itemRepository;
  }

  //Get items based on pagination
  public ItemPayloadDTO getItems(int page, int size, String category, String name) {
    try {
      if (page < 1 || size < 1) {
        throw new
            IllegalArgumentException("Page and size must be greater than 0");
      }

      //Split category string into individual categories
      List<String> categories = Arrays.asList(category.split(","));

      Page<Item> itemPage;
      page -= 1; //Pagination starts with 0, in the frontend we start with 1

      if ((category != null && !category.isEmpty())
          || (name != null && !name.isEmpty())) {
        //If search criteria is provided
        itemPage = itemRepository
            .findByColumnContainingValuesAndFilter(categories,
                name, PageRequest.of(page, size));
      } else {
        itemPage = itemRepository.findAll(PageRequest.of(page, size));
      }
      return new
          ItemPayloadDTO(itemPage.getContent(), itemPage.getTotalElements());
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
