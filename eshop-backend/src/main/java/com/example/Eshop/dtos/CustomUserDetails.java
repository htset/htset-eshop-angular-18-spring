package com.example.Eshop.dtos;

import com.example.Eshop.models.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Data
public class CustomUserDetails implements UserDetails {
  private Long id;
  private String username;
  private String password;
  private String role;
  private String status;
  private String token;
  private Collection<GrantedAuthority> authorities;

  public CustomUserDetails(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.role = user.getRole();
    this.status = user.getStatus();
    this.token = user.getToken();
    this.authorities = new ArrayList<GrantedAuthority>();
    this.authorities.add(new SimpleGrantedAuthority(user.getRole()));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    String role = "ROLE_" + this.role;
    return Collections.singletonList(new SimpleGrantedAuthority(role));
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    if(status.equals("active"))
      return true;
    else
      return false;
  }
}
