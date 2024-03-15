package com.group3.ezquiz.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;

import com.group3.ezquiz.exception.InvalidUserException;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.auth.RegisterRequest;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final IUserService userService;

  @GetMapping("/")
  public String getLandingPage(HttpServletRequest request) {
    if (request.getUserPrincipal() != null) {
      return "redirect:/home";
    }
    return "index";
  }

  @GetMapping("/login")
  public String getLoginPage(HttpServletRequest request, Model model) {
    if (request.getUserPrincipal() != null) {
      return "redirect:/home";
    }
    HttpSession session = request.getSession();
    Object attribute = session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
    if (attribute != null) {
      try {
        InvalidUserException error = (InvalidUserException) attribute;
        model.addAttribute("errMsg", error.getMessage());
      } catch (RuntimeException e) {
        model.addAttribute("errMsg", "Invalid username or password!");
      }
    }
    session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
    return "login";
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
      @Valid RegisterRequest user, BindingResult result) throws BindException {

    if (userService.registerUser(user, result).hasErrors()) {
      throw new BindException(result);
    }

    return ResponseEntity.ok(
        MessageResponse.builder()
            .message("Your account was created successfully!")
            .timestamp(LocalDateTime.now())
            .build());
  }

}
