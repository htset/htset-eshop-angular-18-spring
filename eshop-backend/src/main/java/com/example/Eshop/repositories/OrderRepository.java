package com.example.Eshop.repositories;

import com.example.Eshop.models.Order;
import com.example.Eshop.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
//	Page<Order> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName, Pageable pageable);

  @Query("SELECT o FROM Order o WHERE o.firstName LIKE %:search% OR o.lastName LIKE %:search%")
  List<Order> findAllBySearchCriteria(String search, Pageable pageable);

  @Query("SELECT COUNT(o) FROM Order o WHERE o.firstName LIKE %:search% OR o.lastName LIKE %:search%")
  int countBySearchCriteria(String search);

  @Query("SELECT COUNT(o) FROM Order o")
  int countAll();

}