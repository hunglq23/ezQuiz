package com.group3.ezquiz.service;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.model.dto.UserDTO;
import com.group3.ezquiz.payload.UserRequest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
  void registerUser(UserRequest user);

  User getUserRequesting(HttpServletRequest http);

  Page<User> getListUser(HttpServletRequest http, String email, Pageable page);

  UserDTO getUserById(Long id);

  UserDTO update(UserDTO user, Long id);

  void delete(Long id);
  User getUserByEmail(String email);

}
