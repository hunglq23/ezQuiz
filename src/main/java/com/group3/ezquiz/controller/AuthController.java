package com.group3.ezquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group3.ezquiz.payload.UserRequest;
import com.group3.ezquiz.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @GetMapping("/")
  public String landingPage() {
    return "index";
  }

  @GetMapping("/register")
  public String register(Model model) {
    model.addAttribute("user", new UserRequest());
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(UserRequest user) {
    userService.registerUser(user);
    return "redirect:/login";
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

}
