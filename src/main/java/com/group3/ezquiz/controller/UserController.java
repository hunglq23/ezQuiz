package com.group3.ezquiz.controller;

import com.group3.ezquiz.payload.UserDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/home")
  public String getHomePage(HttpServletRequest http, Model model) {
    User userRequesting = userService.getUserRequesting(http);
    model.addAttribute("user", userRequesting);
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
      @RequestParam(required = false, defaultValue = "", name = "email") String email,
      @RequestParam(required = false, defaultValue = "all", name = "status") String statusReq) {
    Boolean status = Objects.equals(statusReq, "all") ? null : Boolean.valueOf(statusReq);
    Page<User> userList = userService.getListUser(http, email, status, PageRequest.of(page, 3));
    model.addAttribute("userList", userList);
    model.addAttribute("items", userList.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", userList.getTotalPages());
    model.addAttribute("search", email);
    model.addAttribute("status", statusReq);
    return "admin/user-list";
  }

  @GetMapping("/admin/create")
  public String showCreateUserForm(Model model) {
    model.addAttribute("user", new UserDto());
    return "admin/user-create-form";
  }

  @PostMapping("/admin/create")
  public String createUser(HttpServletRequest http, Model model,
      @Valid @ModelAttribute("user") UserDto userDto,
      BindingResult bindingResult) {
    // process the form data
    if (bindingResult.hasErrors()) {
      model.addAttribute("user", userDto);
      return "/admin/user-create-form";
    }
    User checkUser = userService.getUserByEmail(userDto.getEmail());
    if (checkUser != null) {
      model.addAttribute("user", userDto);
      FieldError error = new FieldError("user", "email",
          "Email duplicate.");
      bindingResult.addError(error);
      return "/admin/user-create-form";
    }
    userService.createUser(http, userDto);
    // redirect to the user list page after creating a new user
    return "redirect:/admin/list";
  }

  @GetMapping("/admin/edit/{id}")
  public String getUserUpdate(Model model,
      @PathVariable Long id) {
    User user = userService.getUserById(id);
    model.addAttribute("user", user);
    return "/admin/user-editing";
  }

  @PostMapping("/admin/update/{id}")
  public String update(HttpServletRequest http, Model model, RedirectAttributes redirectAttributes,
      @Valid @ModelAttribute("user") UserDto user,
      BindingResult bindingResult,
      @PathVariable Long id) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("user", user);
      return "/admin/user-editing";
    }
    User checkUser = userService.getUserByEmail(user.getEmail());
    if (checkUser != null) {
      model.addAttribute("user", user);
      FieldError error = new FieldError("user", "email",
          "Email duplicate.");
      bindingResult.addError(error);
      return "/admin/user-editing";
    }
    userService.update(http, user, id);
    return "redirect:/admin/list";
  }

  @GetMapping("/admin/delete/{id}")
  public String delete(
      @PathVariable(name = "id") Long id) {
    userService.delete(id);
    return "redirect:/admin/list";
  }
}
