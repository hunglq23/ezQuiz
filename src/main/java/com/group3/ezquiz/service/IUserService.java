package com.group3.ezquiz.service;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.UserDto;
import com.group3.ezquiz.payload.auth.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

public interface IUserService {

  BindingResult registerUser(RegisterRequest user, BindingResult bindingResult);

  User getByEmail(String email);

  User getUserRequesting(HttpServletRequest http);

  void verifyAccount(String email);

  boolean checkEmailExist(String email);

  User findLearnerByEmail(String email);

  Page<User> getListUser(HttpServletRequest http, String email, Boolean status, Pageable page);

  void createUser(HttpServletRequest request, UserDto userDto);

  User getUserById(Long id);

  void update(HttpServletRequest request, UserDto user, Long id);

  void updatePassword(String email, String pass);

  Boolean isCreator(HttpServletRequest request, User user);

}
