package com.group3.ezquiz.service;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.UserDto;
import com.group3.ezquiz.payload.auth.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IUserService {

  ResponseEntity<?> registerUser(RegisterRequest user);

  User getUserRequesting(HttpServletRequest http);

  Page<User> getListUser(HttpServletRequest http, String email, Boolean status,
      Pageable page);

  void createUser(HttpServletRequest request, UserDto userDto);

  User getUserById(Long id);

  void update(HttpServletRequest request, UserDto user, Long id);

  void delete(Long id);

  void updatePassword(String email, String pass);

  boolean checkEmail(String email);

User findUserByEmail(String email);
}
