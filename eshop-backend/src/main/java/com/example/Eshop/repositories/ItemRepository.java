package com.example.Eshop.repositories;

import com.example.Eshop.models.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
  @Query("SELECT i FROM Item i " +
      "WHERE (i.category IN :categories) " +
      "AND (i.name LIKE %:name%)")
  Page<Item> findByColumnContainingValuesAndFilter(
      @Param("categories") List<String> categories,
      @Param("name") String name,
      Pageable pageable);
}
