package com.group3.ezquiz.service;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.UserRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
  void registerUser(UserRequest user);

  User getUserRequesting(HttpServletRequest http);

  User getUserByEmail(String email);

}
