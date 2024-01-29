package com.group3.ezquiz.controller;

import com.group3.ezquiz.payload.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

  @GetMapping("/admin/list")
  public String userManagement(HttpServletRequest http, Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false, defaultValue = "", name = "email") String email) {
    Page<User> userList = userService.getListUser(http, email, PageRequest.of(page, 3));
    model.addAttribute("userList", userList);
    model.addAttribute("items", userList.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", userList.getTotalPages());
    model.addAttribute("search", email);
    return "admin/user-list";
  }

  @GetMapping("/admin/create")
  public String showCreateQuizForm(Model model) {
    model.addAttribute("user", new UserDto());
    return "admin/user-create-form";
  }

  @PostMapping("/admin/create")
  public String createQuiz(HttpServletRequest http, UserDto userDto) {
    // process the form data
    userService.createUser(http, userDto);
    // redirect to the user list page after creating a new user
    return "redirect:/admin/list";
  }
}
