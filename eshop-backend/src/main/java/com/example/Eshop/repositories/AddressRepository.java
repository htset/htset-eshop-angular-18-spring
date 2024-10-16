package com.example.Eshop.repositories;

import com.example.Eshop.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
  List<Address> findByUserId(Long userId);
}