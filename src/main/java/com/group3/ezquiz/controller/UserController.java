package com.group3.ezquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

  @GetMapping("/home")
  public String getLearnerHomepage() {
    return "home";
  }

  @GetMapping("/profile")
  public String getProfile() {
    return "profile";
  }

}
