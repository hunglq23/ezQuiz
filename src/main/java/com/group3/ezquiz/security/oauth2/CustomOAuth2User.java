package com.group3.ezquiz.security.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.group3.ezquiz.exception.InvalidUserException;
import com.group3.ezquiz.model.Role;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

  private final OAuth2User oAuth2User;
  private final UserRepo userRepo;

  @Override
  public Map<String, Object> getAttributes() {
    return oAuth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    String email = oAuth2User.getAttribute("email");
    User userByEmail = userRepo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(email));

    if (userByEmail.getIsVerified() == false) {
      throw new InvalidUserException("Please check your email to verify!");
    }
    if (userByEmail.getIsEnable() == false) {
      throw new InvalidUserException("Sorry, your account was blocked!");
    }

    Role role = userByEmail.getRole();
    return Collections
        .singleton(new SimpleGrantedAuthority("ROLE_" + role));
  }

  @Override
  public String getName() {
    return oAuth2User.getAttribute("email");
  }

  public String getFullName() {
    return oAuth2User.getAttribute("name");
  }

  public String getEmail() {
    return oAuth2User.getAttribute("email");
  }

}
