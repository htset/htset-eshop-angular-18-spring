package com.example.Eshop.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtRequestFilter jwtRequestFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  public SecurityConfig(JwtRequestFilter jwtRequestFilter,
                        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
    this.jwtRequestFilter = jwtRequestFilter;
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
      throws Exception {
    http
        //Disable CSRF since this is a stateless REST API
        .csrf(csrf -> csrf.disable())
        //Enable CORS
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        //Define public endpoints that can be accessed without authentication
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/**").hasRole("ADMIN")
                .requestMatchers(
                    "/auth/**",
                    "/items/**",
                    "/images/**",
                    "/remoteLogging/**").permitAll()  // Allow access to auth endpoints
                .anyRequest().authenticated() // All other endpoints get authentication
        )
        //Add JWT token filter before UsernamePasswordAuthenticationFilter
		    .addFilterBefore(jwtRequestFilter,
                UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    //Allowed origins
    config.setAllowedOrigins(List.of("http://localhost:4200"));
    //Allowed HTTP methods
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    //Allowed headers
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    //Allow credentials (cookies, authorization headers)
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source
        = new UrlBasedCorsConfigurationSource();
    //Apply CORS to all endpoints
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public AuthenticationManager
    authenticationManager(AuthenticationConfiguration authConfiguration)
      throws Exception {
    return authConfiguration.getAuthenticationManager();
  }

}