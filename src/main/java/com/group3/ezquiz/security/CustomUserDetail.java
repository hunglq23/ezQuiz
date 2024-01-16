package com.group3.ezquiz.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.repository.UserRepo;

@Service
public class CustomUserDetail implements UserDetailsService {

  @Autowired
  private UserRepo userRepo;

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {

    User foundUser = userRepo.findByEmail(email);
    if (foundUser == null) {
      throw new UsernameNotFoundException(email);
    }
    return new org.springframework.security.core.userdetails.User(
        email,
        foundUser.getPassword(),
        Collections
            .singleton(new SimpleGrantedAuthority("ROLE_" + foundUser.getRole())));
  }

}
