package com.example.Eshop.services;

import com.example.Eshop.dtos.CustomUserDetails;
import com.example.Eshop.models.User;
import com.example.Eshop.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public CustomUserDetailsService(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public CustomUserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    //Get user from the database
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found: " + username);
    }

    //Return the custom UserDetails implementation
    return new CustomUserDetails(user);
  }
}
