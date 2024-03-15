package com.group3.ezquiz.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.group3.ezquiz.exception.InvalidUserException;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.repository.UserRepo;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserRepo userRepo;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String email = authentication.getName();
    String password = authentication.getCredentials().toString();

    User user = userRepo.findByEmail(email).orElse(null);

    if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
      throw new BadCredentialsException("Invalid email and password!");
    }

    if (user.getIsVerified() == false) {
      throw new InvalidUserException("Please check your email to verify!");
    } else if (user.getIsEnable() == false) {
      throw new InvalidUserException("Sorry, your account was blocked!");
    }

    return new UsernamePasswordAuthenticationToken(
        user.getEmail(),
        user.getPassword(),
        Collections
            .singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

}
