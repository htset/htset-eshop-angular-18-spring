package com.example.Eshop.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private BigDecimal price;
  private String category;
  private String description;

  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<Image> images;
}