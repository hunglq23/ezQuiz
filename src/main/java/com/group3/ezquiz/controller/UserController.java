package com.group3.ezquiz.controller;

import com.group3.ezquiz.payload.UserDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

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

  @GetMapping("/admin/list")
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

  @GetMapping("/admin/create")
  public String showCreateUserForm(Model model) {
    model.addAttribute("user", new UserDto());
    return "admin/user-create-form";
  }

  @PostMapping("/admin/create")
  public String createUser(HttpServletRequest http, UserDto userDto) {
    // process the form data
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
  public String update(HttpServletRequest http, Model model,
      @PathVariable(name = "id") Long id, UserDto user) {
    userService.update(http, user, id);
    return "redirect:/admin/list";
  }

  @GetMapping("/admin/delete/{id}")
  public String delete(
      @PathVariable(name = "id") Long id) {
    userService.delete(id);
    return "redirect:/admin/list";
  }

  @GetMapping("/profile")
  public String getProfilePage(HttpServletRequest http, Model model) {
    User userRequesting = userService.getUserRequesting(http);
    model.addAttribute("user", userRequesting);
    return "profile";
  }

  @PostMapping ("/user/update-password")
  public String updatePass( HttpServletRequest http, Model model,
                            @Valid @ModelAttribute("oldPass") String oldPass,
                            @Valid @ModelAttribute("newPass") String newPass,
                            @Valid @ModelAttribute("reNewPass") String reNewPass, RedirectAttributes redirectAttributes) {
    User userRequesting = userService.getUserRequesting(http);
    boolean isPasswordCorrect = passwordEncoder.matches(oldPass, userRequesting.getPassword());
    model.addAttribute("user", userRequesting);
    String successMessage = "updated fail!";
    if(isPasswordCorrect && newPass.equals(reNewPass)) {
      userService.updatePassword(userRequesting.getEmail(), newPass);
      model.addAttribute("message", "success");
      successMessage = "updated successfully!";
    }
    redirectAttributes.addFlashAttribute("successMessage", successMessage);
    return "redirect:/profile";
  }
}
