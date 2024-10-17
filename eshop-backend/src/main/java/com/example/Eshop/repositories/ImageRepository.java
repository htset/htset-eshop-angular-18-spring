package com.example.Eshop.repositories;

import com.example.Eshop.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
  Optional<Image> findByItemId(Long itemId);
}