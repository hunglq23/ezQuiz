package com.group3.ezquiz.controller;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.UserDto;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final IUserService userService;

  @GetMapping("/list")
  public String userManagement(HttpServletRequest http, Model model,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "", name = "email") String email,
      @RequestParam(required = false, defaultValue = "all", name = "status") String statusReq) {
    Boolean status = Objects.equals(statusReq, "all") ? null : Boolean.valueOf(statusReq);
    Page<User> userList = userService.getListUser(http, email, status, PageRequest.of(page, 2));
    model.addAttribute("userList", userList);
    model.addAttribute("items", userList.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", userList.getTotalPages());
    model.addAttribute("search", email);
    model.addAttribute("status", statusReq);
    return "admin/user-list";
  }

  @GetMapping("/create")
  public String showCreateUserForm(Model model) {
    model.addAttribute("user", new UserDto());
    return "admin/user-create-form";
  }

  @PostMapping("/create")
  public String createUser(HttpServletRequest http, UserDto userDto) {
    // process the form data
    userService.createUser(http, userDto);
    // redirect to the user list page after creating a new user
    return "redirect:/admin/list";
  }

  @GetMapping("/edit/{id}")
  public String getUserUpdate(Model model,
      @PathVariable Long id) {
    User user = userService.getUserById(id);
    model.addAttribute("user", user);
    return "/admin/user-editing";
  }

  @PostMapping("/update/{id}")
  public String update(HttpServletRequest http, Model model,
      @PathVariable(name = "id") Long id, UserDto user) {
    userService.update(http, user, id);
    return "redirect:/admin/list";
  }

  @GetMapping("/delete/{id}")
  public String delete(
      @PathVariable(name = "id") Long id) {
    userService.delete(id);
    return "redirect:/admin/list";
  }

}
