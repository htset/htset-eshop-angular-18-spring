package com.example.Eshop.controllers;

import com.example.Eshop.config.JwtUtilities;
import com.example.Eshop.config.MailConfig;
import com.example.Eshop.dtos.*;
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

import java.util.Base64;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private AuthenticationManager authenticationManager;
  private CustomUserDetailsService userDetailsService;
  private UserService userService;
  private JwtUtilities jwtUtilities;
  private PasswordEncoder passwordEncoder;
  private MailConfig mailConfig;

  public AuthController(AuthenticationManager authenticationManager,
                        CustomUserDetailsService userDetailsService,
                        UserService userService, JwtUtilities jwtUtilities,
                        PasswordEncoder passwordEncoder,
                        MailConfig mailConfig){
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.userService = userService;
    this.jwtUtilities = jwtUtilities;
    this.passwordEncoder = passwordEncoder;
    this.mailConfig = mailConfig;
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

      if(!userDetails.isEnabled()){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Registration has not been confirmed");
      }

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

  //Register new user
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    try {
      //Check if the username or email already exists
      if (userService.getUserByUsername(user.getUsername()) != null) {
        return ResponseEntity.badRequest().body("Username already exists");
      }
      if (userService.getUserByEmail(user.getEmail()) != null) {
        return ResponseEntity.badRequest().body("Email already exists");
      }

      //Hash the password before saving
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      user.setRole("CUSTOMER");
      user.setStatus("PENDING");
      user.setRegistrationCode(createConfirmationToken());

      User createdUser = userService.updateUser(user);

      sendConfirmationEmail(createdUser);

      return ResponseEntity.ok(createDTO(createdUser));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Registration failed: " + e.getMessage());
    }
  }

  @PostMapping("/confirm_registration")
  public ResponseEntity<?> confirmRegistration(@RequestBody RegistrationCodeDTO code) {
    try{
      // Retrieve user by registration code
      User user = userService.getUserByRegistrationCode(code.getCode());

      if (user == null) {
        return ResponseEntity.badRequest().body("Registration code not found");
      }

      // Check if the user is already activated
      if ("ACTIVE".equals(user.getStatus())) {
        return ResponseEntity.badRequest().body("User is already activated");
      }

      // Activate user and generate tokens
      user.setStatus("ACTIVE");
      String token = jwtUtilities
          .generateToken(user.getUsername(), user.getId(), user.getRole());
      String refreshToken = jwtUtilities
          .generateRefreshToken(user.getUsername());
      user.setToken(token);
      user.setRefreshToken(refreshToken);
      user.setRefreshTokenExpiry(new Date(System.currentTimeMillis()
          + 1000 * 60 * 60 * 24 * 30));  //30 days expiry

      userService.updateUser(user);  //Save user changes

      return ResponseEntity.ok(createDTO(user));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to confirm registration: " + e.getMessage());
    }
  }

  @PostMapping("/reset_password")
  public ResponseEntity<?> resetPassword(@RequestBody ResetEmailDTO resetEmail) {
    try{
      // Retrieve user by email
      User user = userService.getUserByEmail(resetEmail.getEmail());

      if (user == null) {
        return ResponseEntity.badRequest().body("Email not found");
      }

      // Mark user for password reset and generate a registration code
      user.setStatus("RESET");
      user.setPassword(null);
      user.setRegistrationCode(createConfirmationToken());

      userService.updateUser(user);

      // Send password reset email
      sendPasswordResetEmail(user);

      return ResponseEntity.ok(createDTO(user));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to reset password: " + e.getMessage());
    }
  }

  @PostMapping("/change_password")
  public ResponseEntity<?> changePassword(@RequestBody User inputUser) {
    try{
      //Get user by registration code
      User user = userService
          .getUserByRegistrationCode(inputUser.getRegistrationCode());

      if (user == null) {
        return ResponseEntity.badRequest().body("User not found");
      }

      //Hash new password and update user status
      String hashedPassword = passwordEncoder.encode(inputUser.getPassword());
      user.setPassword(hashedPassword);
      user.setStatus("ACTIVE");

      //Generate tokens
      String token = jwtUtilities
          .generateToken(user.getUsername(), user.getId(), user.getRole());
      String refreshToken = jwtUtilities
          .generateRefreshToken(user.getUsername());
      user.setToken(token);
      user.setRefreshToken(refreshToken);
      user.setRefreshTokenExpiry(new Date(System.currentTimeMillis()
          + 1000 * 60 * 60 * 24 * 30));  //30 days expiry

      userService.updateUser(user);  //Save changes

      return ResponseEntity.ok(createDTO(user));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to change password: " + e.getMessage());
    }
  }

  ///////////////////////////////////////
  //  Private methods
  ///////////////////////////////////////

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

  public String createConfirmationToken() {
    byte[] randomNum = new byte[64];
    SecureRandom secureRandom = new SecureRandom();
    //Fill array with secure random bytes
    secureRandom.nextBytes(randomNum);
    //Encode bytes to Base64
    String tempString = Base64.getEncoder().encodeToString(randomNum);
    //Replace problematic characters
    return tempString
        .replace("+", "")
        .replace("=", "")
        .replace("/", "");
  }

  private void sendConfirmationEmail(User user) {
    //Get mail server properties
    Properties props = new Properties();
    props.put("mail.smtp.host", mailConfig.getSmtpHost());
    props.put("mail.smtp.port", mailConfig.getSmtpPort());
    props.put("mail.smtp.auth", String.valueOf(mailConfig.isSmtpAuth()));
    props.put("mail.smtp.starttls.enable",
        String.valueOf(mailConfig.isStarttlsEnable()));
    props.put("mail.smtp.ssl.trust", mailConfig.getSslTrust());

    //Create a session with an authenticator
    Session session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mailConfig.getSmtpUsername(),
            mailConfig.getSmtpPassword());
      }
    });

    try {
      //Create Message
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress("info@my-eshop.com"));
      message.setSubject("Confirm Registration");
      message.setContent(
          "To confirm registration please click "
              +" <a href=\"http://localhost:4200/confirm_registration?code="
              + user.getRegistrationCode()
              + "\">here</a>",
          "text/html" //Body is HTML
      );
      message.setRecipient(Message.RecipientType.TO,
          new InternetAddress(user.getEmail()));

      //Send message
      Transport.send(message);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  private void sendPasswordResetEmail(User user) {
    //Get mail server properties
    Properties props = new Properties();
    props.put("mail.smtp.host", mailConfig.getSmtpHost());
    props.put("mail.smtp.port", mailConfig.getSmtpPort());
    props.put("mail.smtp.auth", String.valueOf(mailConfig.isSmtpAuth()));
    props.put("mail.smtp.starttls.enable",
        String.valueOf(mailConfig.isStarttlsEnable()));
    props.put("mail.smtp.ssl.trust", mailConfig.getSslTrust());

    //Create a session with an authenticator
    Session session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mailConfig.getSmtpUsername(),
            mailConfig.getSmtpPassword());
      }
    });

    try {
      // Create Message
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress("info@my-eshop.com"));
      message.setSubject("Email Reset");
      message.setContent(
          "To insert a new password, please click "
              + " <a href=\"http://localhost:4200/new_password?code="
              + user.getRegistrationCode()
              + "\">here</a>",
          "text/html" //Body is HTML
      );
      message.setRecipient(Message.RecipientType.TO,
          new InternetAddress(user.getEmail()));

      //Send message
      Transport.send(message);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
}
