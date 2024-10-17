package com.example.Eshop.services;

import com.example.Eshop.models.User;
import com.example.Eshop.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
  private UserRepository userRepository;
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public List<User> getAllUsers(){
    return userRepository.findAll();
  }

  public User getUserByRegistrationCode(String registrationCode) {
    return userRepository.findByRegistrationCode(registrationCode);
  }


  public User updateUser(User user) {
    return userRepository.save(user);  //Save or update the user
  }
}
