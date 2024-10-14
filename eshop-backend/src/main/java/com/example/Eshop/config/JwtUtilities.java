package com.example.Eshop.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtilities {
  private final SecretKey secretKey
      = Keys.secretKeyFor(SignatureAlgorithm.HS512);

  //Generate token with user claims (username, userId, role)
  public String generateToken(String username, Long userId, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    claims.put("roles", role);
    return createToken(claims, username);
  }

  //Create the token with the given claims and subject (username)
  public String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        //Token valid for 12 hours
		    .setExpiration(new Date(System.currentTimeMillis()
            + 1000 * 60 * 60 * 12))
        .signWith(secretKey, SignatureAlgorithm.HS512)
        .compact();
  }
}
