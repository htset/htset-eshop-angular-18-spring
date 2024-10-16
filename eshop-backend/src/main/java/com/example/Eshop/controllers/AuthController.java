package com.example.Eshop.controllers;

import com.example.Eshop.config.JwtUtilities;
import com.example.Eshop.dtos.AuthRequestDTO;
import com.example.Eshop.dtos.CustomUserDetails;
import com.example.Eshop.dtos.TokenRefreshDTO;
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

import java.util.Date;

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

      //Generate JWT tokens
      String token = jwtUtilities.generateToken(userDetails.getUsername(),
          userDetails.getId(), userDetails.getRole());
      String refreshToken = jwtUtilities
          .generateRefreshToken(userDetails.getUsername());

      //Save token to database
      User user = userService.getUserById(userDetails.getId());
      user.setToken(token);
      user.setRefreshToken(refreshToken);
      user.setRefreshTokenExpiry(new Date(System.currentTimeMillis()
          + 1000L * 60 * 60 * 24 * 30)); // 30-day expiry
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

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshDTO request) {
    try{
      String refreshToken = request.getRefreshToken();
      if (jwtUtilities.validateRefreshToken(refreshToken)) {
        String username = jwtUtilities.extractUsername(refreshToken);
        CustomUserDetails userDetails
            = userDetailsService.loadUserByUsername(username);

        //Generate new token
        String newToken = jwtUtilities.generateToken(userDetails.getUsername(),
            ((CustomUserDetails) userDetails).getId(), userDetails.getRole());

        //Save updated tokens to database
        User user = userService.getUserById(userDetails.getId());
        user.setToken(newToken);
        userService.updateUser(user);

        return ResponseEntity.ok(this.createDTO(user));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Invalid refresh token");
      }
    } catch (UsernameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("User not found");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Refresh token failed:" + e.getMessage());
    }
  }

  @PostMapping("/revoke")
  public ResponseEntity<?> revokeToken(@RequestBody TokenRefreshDTO request) {
    try {
      String refreshToken = request.getRefreshToken();
      if (jwtUtilities.validateRefreshToken(refreshToken)) {
        String username = jwtUtilities.extractUsername(refreshToken);
        CustomUserDetails userDetails
            = userDetailsService.loadUserByUsername(username);

        //Revoke the tokens by setting them to null
        User user = userService.getUserById(userDetails.getId());
        user.setToken(null);
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);

        //Update the user in the database
        userService.updateUser(user);

        return ResponseEntity.ok(this.createDTO(user));
      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error revoking token");
      }
    } catch (UsernameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("User not found");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Revoke token failed: " + e.getMessage());
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
    userResponse.setRefreshToken(user.getRefreshToken());
    userResponse.setRefreshTokenExpiry(user.getRefreshTokenExpiry());
    return userResponse;
  }
}
