package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/list")
  public String userManagement(HttpServletRequest http, Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false, defaultValue = "", name = "email") String email) {
    Page<User> userList = userService.getListUser(http, email, PageRequest.of(page, 3));
    model.addAttribute("userList", userList);
    model.addAttribute("items", userList.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", userList.getTotalPages());
    model.addAttribute("search", email);
    return "list-user";
  }

  @GetMapping("/update")
  public String getUserUpdate(HttpServletRequest http, Model model,
                              @RequestParam(name = "id") Long id) {
    UserDTO user = userService.getUserById(id);
    model.addAttribute("userDTO", user);
    return "update";
  }

  @PostMapping("/admin/user-management/{id}")
  public String update(HttpServletRequest http, Model model,
                       @PathVariable(name = "id") Long id, UserDTO user) {
    userService.update(user, id);
    return "redirect:/list";
  }

  @GetMapping("/delete/{userId}")
  public String delete(HttpServletRequest http, Model model,
                       @PathVariable(name = "userId") Long userId) {
    userService.delete(userId);
    return "redirect:/list";
  }
}
