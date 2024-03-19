package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.EmailService;
import com.group3.ezquiz.service.IUserService;
import com.group3.ezquiz.service.JwtService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Controller
@RequiredArgsConstructor
public class PasswordController {

  private final IUserService userService;
  private final EmailService emailService;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/forgot-password")
  public String getForgotPasswordPage(HttpServletRequest request, Model model) {
    return "forgot-password";
  }

  @PostMapping("/send-forgot-password")
  public String sendForgotPassword(HttpServletRequest request, Model model,
      @Valid @ModelAttribute("username") String email) throws IOException, MessagingException {
    boolean isExistEmail = userService.checkEmailExist(email);
    if (!isExistEmail) {
      return "redirect:/forgot-password?error";
    }
    String subject = "ezQuiz Password Reset";
    Resource resource = new ClassPathResource("static/email/email-forgot-pass.html");
    Scanner scanner = new Scanner(resource.getInputStream(), StandardCharsets.UTF_8);
    String content = scanner.useDelimiter("\\A").next();
    scanner.close();
    String token = jwtService.generateToken(email);
    content = content.replace("[token]", token);
    emailService.sendSimpleMessage(email, subject, content);
    return "redirect:/forgot-password?emailSent";
  }

  @GetMapping("/reset-forgot-password")
  public String resetForgotPassword(
      HttpServletRequest request,
      Model model,
      @RequestParam(value = "token") String token) {
    String email = jwtService.getEmailFromToken(token);
    model.addAttribute("email", email);
    model.addAttribute("token", token);
    return "password-reset";
  }

  @PostMapping("/update-forgot-password")
  public String updateForgotPasswordPage(
      HttpServletRequest request, Model model,
      @RequestParam("email") String email,
      @RequestParam("token") String token,
      @ModelAttribute("password") String password,
      @ModelAttribute("re_password") String rePassword) {
    if (!password.equals(rePassword)) {
      return "redirect:/forgot-password?fail";
    }
    String emailForgot = jwtService.getEmailFromToken(token);
    userService.updatePassword(emailForgot, password);
    return "redirect:/login?passwordChanged";
  }

  @GetMapping("/change-password")
  public String getProfilePage(HttpServletRequest http, Model model) {
    User userRequesting = userService.getUserRequesting(http);
    model.addAttribute("user", userRequesting);
    return "password-change";
  }

  @PostMapping("/update-password")
  public String updatePass(HttpServletRequest http, Model model,
                           @Valid @ModelAttribute("fullName") String fullName,
                           @Valid @ModelAttribute("note") String note,
      @Valid @ModelAttribute("oldPass") String oldPass,
                           @Valid   @ModelAttribute("newPass") String newPass,
      @Valid @ModelAttribute("reNewPass") String reNewPass, RedirectAttributes redirectAttributes, BindingResult result) {
    User userRequesting = userService.getUserRequesting(http);
    userRequesting.setFullName(fullName);
    userRequesting.setNote(note);
    userService.save(userRequesting);
    boolean isPasswordCorrect = passwordEncoder.matches(oldPass, userRequesting.getPassword());
    model.addAttribute("user", userRequesting);
    String successMessage = "Updated information successfully! Check your password again!";
    if (isPasswordCorrect && newPass.equals(reNewPass)) {
      userService.updatePassword(userRequesting.getEmail(), newPass);
      model.addAttribute("message", "success");
      successMessage = "Updated successfully!";
    }
    redirectAttributes.addFlashAttribute("successMessage", successMessage);
    return "redirect:/change-password";
  }
}
