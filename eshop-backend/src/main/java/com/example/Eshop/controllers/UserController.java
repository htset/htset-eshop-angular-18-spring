package com.example.Eshop.controllers;

import com.example.Eshop.models.User;
import com.example.Eshop.services.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  private UserService userService;

  public UserController(UserService userService){
    this.userService = userService;
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

}
