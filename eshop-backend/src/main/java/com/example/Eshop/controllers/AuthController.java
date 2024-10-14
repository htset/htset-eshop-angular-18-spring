package com.example.Eshop.controllers;

import com.example.Eshop.config.JwtUtilities;
import com.example.Eshop.dtos.AuthRequestDTO;
import com.example.Eshop.dtos.CustomUserDetails;
import com.example.Eshop.dtos.UserDTO;
import com.example.Eshop.models.User;
import com.example.Eshop.services.CustomUserDetailsService;
import com.example.Eshop.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private AuthenticationManager authenticationManager;
  private CustomUserDetailsService userDetailsService;
  private UserService userService;
  private JwtUtilities jwtUtilities;
  private PasswordEncoder passwordEncoder;

  public AuthController(AuthenticationManager authenticationManager,
                        CustomUserDetailsService userDetailsService,
                        UserService userService, JwtUtilities jwtUtilities,
                        PasswordEncoder passwordEncoder){
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.userService = userService;
    this.jwtUtilities = jwtUtilities;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequest)
      throws Exception {
    //Use username and password to authenticate user
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
              authRequest.getPassword())
      );

      CustomUserDetails userDetails =
          (CustomUserDetails)userDetailsService
              .loadUserByUsername(authRequest.getUsername());

      //Generate JWT token
      String token = jwtUtilities.generateToken(userDetails.getUsername(),
          userDetails.getId(), userDetails.getRole());

      //Save token to database
      User user = userService.getUserById(userDetails.getId());
      user.setToken(token);
      userService.updateUser(user);

      return ResponseEntity.ok(this.createDTO(user));
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid credentials");
    } catch (UsernameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid credentials");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Authentication failed: " + e.getMessage());
    }
  }

  //Create user DTO for the response
  private UserDTO createDTO(User user){
    UserDTO userResponse = new UserDTO();
    userResponse.setId(user.getId());
    userResponse.setUsername(user.getUsername());
    userResponse.setStatus(user.getStatus());
    userResponse.setRole(user.getRole());
    userResponse.setToken(user.getToken());
    return userResponse;
  }
}
