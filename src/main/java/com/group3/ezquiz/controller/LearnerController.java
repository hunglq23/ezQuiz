package com.group3.ezquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LearnerController {
  @GetMapping("/home")
  public String getLearnerHomepage() {
    return "home";
  }

}
