package com.group3.ezquiz.controller;

import java.net.BindException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.group3.ezquiz.payload.RegisterRequest;
import com.group3.ezquiz.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @GetMapping("/")
  public String getLandingPage(HttpServletRequest request) {
    if (request.getUserPrincipal() != null) {
      return "redirect:/home";
    }
    return "index";
  }

  @GetMapping("/register")
  public String getRegisterPage(HttpServletRequest request) {
    if (request.getUserPrincipal() != null) {
      return "redirect:/home";
    }
    return "register";
  }

  @PostMapping("/register")
  public ResponseEntity<?> submitRegisterForm(
      @Valid RegisterRequest user) throws BindException {

    return userService.registerUser(user);
  }

  @GetMapping("/login")
  public String getLoginPage(HttpServletRequest request) {
    if (request.getUserPrincipal() != null) {
      return "redirect:/home";
    }
    return "login";
  }

}
