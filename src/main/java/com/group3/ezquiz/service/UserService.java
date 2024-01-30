package com.group3.ezquiz.service;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.UserDto;
import com.group3.ezquiz.payload.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  void registerUser(RegisterRequest user);

  User getUserRequesting(HttpServletRequest http);

  User getUserByEmail(String email);

  Page<User> getListUser(HttpServletRequest http, String email, Pageable page);

  void createUser(HttpServletRequest request, UserDto userDto);
}
