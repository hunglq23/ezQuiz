package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.UserDto;
import com.group3.ezquiz.payload.auth.RegisterRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.InvalidEmailException;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Role;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepo userRepo;

  @Override
  public ResponseEntity<?> registerUser(RegisterRequest regUser) {

    validateEmail(regUser.getEmail());

    String encodedPass = passwordEncoder.encode(regUser.getPassword());

    userRepo.save(
        User.builder()
            .email(regUser.getEmail())
            .fullName(regUser.getFullName())
            .password(encodedPass)
            .isEnable(true)
            .isVerified(false)
            .role(Role.LEARNER)
            .build());

    return ResponseEntity.ok(
        MessageResponse.builder()
            .message("Your account was created successfully!")
            .timestamp(LocalDateTime.now())
            .build());
  }

  private void validateEmail(String email) {
    Boolean emailExisted = userRepo.findByEmail(email).isPresent();
    if (emailExisted) {
      throw new InvalidEmailException("Email existed!");
    } else {
      String[] permitedEmailDomains = { "@gmail.com", "@fpt.edu.vn", "@email" };
      boolean permited = false;
      for (String domain : permitedEmailDomains) {
        if (email.endsWith(domain)) {
          permited = true;
        }
      }
      if (!permited) {
        String invalidDomain = email.substring(email.indexOf('@') + 1);
        throw new InvalidEmailException("'" + invalidDomain + "' is invalid domain!");
      }
    }
  }

  @Override
  public User getUserRequesting(HttpServletRequest http) {

    Principal userPrincipal = http.getUserPrincipal();
    String email = userPrincipal.getName();
    return getUserByEmail(email);
  }

  @Override
  public Page<User> getListUser(HttpServletRequest http, String email, Boolean status, Pageable page) {
    return userRepo.getAllUser(email, email, status, page);
  }

  @Override
  public void createUser(HttpServletRequest request, UserDto userDto) {
    String encodedPass = passwordEncoder.encode(userDto.getPassword());
    userRepo.save(
        User.builder()
            .role(userDto.getRole())
            .email(userDto.getEmail())
            .fullName(userDto.getFullName())
            .password(encodedPass)
            .isVerified(userDto.getIsVerified())
            .isEnable(userDto.getIsEnable())
            .phone(userDto.getPhone())
            .note(userDto.getNote())
            .build());
  }

  @Override
  public User getUserById(Long id) {

    return userRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found user ID: " + id));
  }

  @Override
  public void update(HttpServletRequest request, UserDto user, Long id) {
    String encodedPass = passwordEncoder.encode(user.getPassword());
    User existedUser = getUserById(id);
    User saveUser = User.builder()
        // unchangeable
        .id(existedUser.getId())
        .createdAt(existedUser.getCreatedAt())
        // .createdBy(existedUser.getCreatedBy())
        // to update
        .role(user.getRole())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .password(encodedPass)
        .isVerified(user.getIsVerified())
        .isEnable(user.getIsEnable())
        .phone(user.getPhone())
        .note(user.getNote())
        // .updatedBy(userRequesting.getId())
        .build();
    userRepo.save(saveUser);
  }

  @Override
  public void delete(Long id) {
    userRepo.deleteById(id);
  }

  private User getUserByEmail(String email) {
    return userRepo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(email));
  }

  @Override
  public void updatePassword(String email, String pass) {
    String encodedPass = passwordEncoder.encode(pass);
    User user = userRepo.findByEmail(email).get();
    user.setPassword(encodedPass);
    userRepo.save(user);
  }

  public boolean checkEmail(String email) {
    return userRepo.existsByEmail(email);
  }
}
