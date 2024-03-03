package com.group3.ezquiz.service;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.ObjectDto;
import com.group3.ezquiz.payload.UserDto;
import com.group3.ezquiz.payload.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
  ResponseEntity<?> registerUser(RegisterRequest user);

  User getUserRequesting(HttpServletRequest http);

  User getUserByEmail(String email);

  Page<User> getListUser(HttpServletRequest http, String email, Boolean status, Pageable page);

  void createUser(HttpServletRequest request, UserDto userDto);

  User getUserById(Long id);

  void update(HttpServletRequest request, UserDto user, Long id);

  void delete(Long id);

  List<ObjectDto> getQuizAndClassroomByUser(HttpServletRequest request, Boolean sortTime);
}
