package com.group3.ezquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.HomeContent;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final IUserService userService;
  private final IQuizService quizService;

  @GetMapping("/home")
  public String getHomePage(HttpServletRequest request, Model model) {
    User userRequesting = userService.getUserRequesting(request);
    HomeContent content = quizService.getHomeContent();

    model.addAttribute("content", content);
    model.addAttribute("user", userRequesting);
    return "home";
  }

}
