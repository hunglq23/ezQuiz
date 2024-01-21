package com.group3.ezquiz.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Role;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.UserRequest;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.UserService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepo userRepo;

  @Override
  public User foundUserByPrincipal(Principal principal) {
    return userRepo.findByEmail(principal.getName());
  }

  @Override
  public void registerUser(UserRequest regUser) {
    // Validation (include pass)
    // Encrypt password
    String encodedPass = passwordEncoder.encode(regUser.getPassword());

    // Save
    userRepo.save(
        User.builder()
            .email(regUser.getEmail())
            .fullName(regUser.getFullName())
            .password(encodedPass)
            .isEnable(false)
            .isVerified(false)
            .role(Role.LEARNER)
            .build());
  }

}
