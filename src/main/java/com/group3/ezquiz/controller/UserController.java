package com.group3.ezquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.HomeContent;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

  @GetMapping("/profile")
  public String getProfilePage(HttpServletRequest request, Model model) {
    User userRequesting = userService.getUserRequesting(request);
    model.addAttribute("user", userRequesting);
    return "profile";
  }

  @PostMapping("/update-profile")
  public String updateProfile(HttpServletRequest http, Model model,
      @Valid @ModelAttribute("fullName") String fullName,
      @Valid @ModelAttribute("phone") String phone,

      RedirectAttributes redirectAttributes, BindingResult result) {
    User userRequesting = userService.getUserRequesting(http);
    userRequesting.setFullName(fullName);
    userRequesting.setPhone(phone);
    userService.save(userRequesting);
    model.addAttribute("user", userRequesting);
    String successMessage = "Updated information successfully!";
    redirectAttributes.addFlashAttribute("successMessage", successMessage);
    return "redirect:/profile";
  }

}
