package com.group3.ezquiz.controller;

import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final IUserService userService;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/home")
  public String getHomePage(HttpServletRequest http, Model model) {
    User userRequesting = userService.getUserRequesting(http);
    model.addAttribute("user", userRequesting);

    return "home";
  }

  @GetMapping("/change-password")
  public String getProfilePage(HttpServletRequest http, Model model) {
    User userRequesting = userService.getUserRequesting(http);
    model.addAttribute("user", userRequesting);
    return "password-change";
  }

  @PostMapping("/update-password")
  public String updatePass(HttpServletRequest http, Model model,
      @Valid @ModelAttribute("oldPass") String oldPass,
      @Valid @ModelAttribute("newPass") String newPass,
      @Valid @ModelAttribute("reNewPass") String reNewPass, RedirectAttributes redirectAttributes) {
    User userRequesting = userService.getUserRequesting(http);
    boolean isPasswordCorrect = passwordEncoder.matches(oldPass, userRequesting.getPassword());
    model.addAttribute("user", userRequesting);
    String successMessage = "Updated failed please check your password again!";
    if (isPasswordCorrect && newPass.equals(reNewPass)) {
      userService.updatePassword(userRequesting.getEmail(), newPass);
      model.addAttribute("message", "success");
      successMessage = "Updated successfully!";
    }
    redirectAttributes.addFlashAttribute("successMessage", successMessage);
    return "redirect:/change-password";
  }
}
