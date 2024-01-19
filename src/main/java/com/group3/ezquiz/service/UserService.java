package com.group3.ezquiz.service;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.UserRequest;

import java.security.Principal;

public interface UserService {
  void registerUser(UserRequest user);

  User foundUserByPrincipal(Principal principal);

}
