package com.group3.ezquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/home")
  public String getLearnerHomepage() {
    return "home";
  }

  @GetMapping("/profile")
  public String getProfile(HttpServletRequest http, Model model) {
    User userRequesting = userService.getUserRequesting(http);
    model.addAttribute("user", userRequesting);
    return "profile";
  }

  @GetMapping()
  public String getString() {
    return new String();
  }

}
