package com.example.Eshop.controllers;

import com.example.Eshop.models.Image;
import com.example.Eshop.models.Item;
import com.example.Eshop.repositories.ImageRepository;
import com.example.Eshop.repositories.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

@RestController
@RequestMapping("/images")
public class ImageController {
  private static String IMAGES_FOLDER = "C://images";

  private final ImageRepository imageRepository;
  private final ItemRepository itemRepository;

  ImageController(ImageRepository repository,
                  ItemRepository itemRepository) {
    this.imageRepository = repository;
    this.itemRepository = itemRepository;
  }

  @PostMapping
  public ResponseEntity<?> imageUpload(@RequestParam("image") MultipartFile file) {

    try{
      if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("Image is empty");
      }

      Path pathToSave = Paths.get(IMAGES_FOLDER);
      String originalFileName = file.getOriginalFilename();
      String fileName = originalFileName != null ? originalFileName.trim() : "uploaded_image";
      Path fullPath = pathToSave.resolve(fileName);
      String dbPath = IMAGES_FOLDER + "/" + fileName;

      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);
      }

      //Parse item ID and image type from file name
      Long itemId = Long.parseLong(fileName.substring(0, fileName.indexOf('.')));
      String fileType = fileName.substring(fileName.indexOf('.') + 1);

      Item item = itemRepository.findById(itemId)
          .orElseThrow(() -> new RuntimeException("Item not found"));

      //Find or create an Image entity in the database
      Image image = imageRepository.findByItemId(itemId).orElse(new Image());
      image.setItem(item);
      image.setFileName(fileName);
      image.setFileType(fileType);

      //Save or update the image entity
      imageRepository.save(image);

      return ResponseEntity.ok(Collections.singletonMap("dbPath", dbPath));

    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to upload the file: " + e.getMessage());
    }
  }
}
