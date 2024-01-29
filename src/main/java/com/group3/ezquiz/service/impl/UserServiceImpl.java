package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.payload.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Role;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.UserRequest;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepo userRepo;

  @Override
  public User getUserRequesting(HttpServletRequest http) {

    Principal userPrincipal = http.getUserPrincipal();
    String email = userPrincipal.getName(); //
    return userRepo.findByEmail(email);
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

  @Override
  public User getUserByEmail(String email) {
    return userRepo.findByEmail(email);
  }


  @Override
  public Page<User> getListUser(HttpServletRequest http, String email, Pageable page) {
    return userRepo.getAllUser(email, email, page);
  }

  @Override
  public void createUser(HttpServletRequest request, UserDto userDto) {
    userRepo.save(
            User.builder()
                    .role(userDto.getRole())
                    .email(userDto.getEmail())
                    .fullName(userDto.getFullName())
                    .password(userDto.getPassword())
                    .isVerified(userDto.getIsVerified())
                    .isEnable(userDto.getIsEnable())
                    .phone(userDto.getPhone())
                    .note(userDto.getNote())
                    .build());
  }


}
