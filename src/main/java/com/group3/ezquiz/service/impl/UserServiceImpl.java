package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.payload.UserDto;

import org.apache.coyote.BadRequestException;
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
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.RegisterRequest;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        new MessageResponse("New account was created successfully!"));
  }

  private void validateEmail(String email) {
    User byEmail = userRepo.findByEmail(email);
    if (byEmail != null) { // email existed
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
  public User getUserByEmail(String email) {
    return userRepo.findByEmail(email);
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
    User userById = userRepo.findUserById(id);
    if (userById != null) {
      // if (userById.getUpdatedBy() == null) {
      // userById.setUpdateAt(null);
      // }
      return userById;
    }
    throw new ResourceNotFoundException("Cannot find user with" + id);
  }

  @Override
  public void update(HttpServletRequest request, UserDto user, Long id) {
    String encodedPass = passwordEncoder.encode(user.getPassword());
    User userRequesting = getUserRequesting(request);
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

}
