package com.example.Eshop.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
  private final JwtUtilities jwtUtil;
  private final UserDetailsService myUserDetailsService;

  public JwtRequestFilter(JwtUtilities jwtUtil,
                          UserDetailsService myUserDetailsService) {
    this.jwtUtil = jwtUtil;
    this.myUserDetailsService = myUserDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain)
      throws ServletException, IOException {

    //Skip filtering for login endpoint
    String path = request.getServletPath();
    if (path.equals("/auth/login")) {
      chain.doFilter(request, response);
      return;
    }

    final String authorizationHeader = request.getHeader("Authorization");
    String username = null;
    String jwt = null;

    // Extract JWT from the Authorization header (if present)
    if (authorizationHeader != null
        && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7); //Remove "Bearer " prefix
      username = jwtUtil.extractUsername(jwt);
    }

    //Validate the token and set authentication
    if (username != null
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails
          = this.myUserDetailsService.loadUserByUsername(username);

      if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
        UsernamePasswordAuthenticationToken authToken
            = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    chain.doFilter(request, response);
  }
}
