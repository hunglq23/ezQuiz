package com.group3.ezquiz.controller;

import com.group3.ezquiz.payload.ObjectDto;
import com.group3.ezquiz.payload.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final IUserService userService;

  @GetMapping("/home")
  public String getHomePage(HttpServletRequest http, Model model) {
    User userRequesting = userService.getUserRequesting(http);
    model.addAttribute("user", userRequesting);

    return "home";
  }

  @GetMapping("/library")
  public String getLibraryPage(
          HttpServletRequest request,
          @RequestParam(required = false, value = "sort") Optional<String> sortOrder,
          @RequestParam(required = false, value = "page") Optional<Integer> page,
          @RequestParam(required = false, value = "size") Optional<Integer> size,
          Model model) {
    String sort = sortOrder.orElse("latest");
    Integer currentPage = page.orElse(1);
    Integer pageSize = size.orElse(3);
    if (currentPage < 1)
      return "redirect:/quiz/my-quiz?sort=" + sort +
              "&page=1&size=" + pageSize;
    Page<ObjectDto> objectDtoList = userService.getQuizAndClassroomByTeacher(
            request, sort, PageRequest.of(currentPage -1, pageSize));
    int maxPage = objectDtoList.getTotalPages();
    if (currentPage > maxPage) {
      return "redirect:/quiz/my-quiz?sort=" + sort +
              "&page=" + maxPage +
              "&size=" + pageSize;
    }
    int curPage = objectDtoList.getNumber() + 1;
    model.addAttribute("object", objectDtoList);
    model.addAttribute("sort", sort);
    model.addAttribute("currentPage", curPage > maxPage ? maxPage : curPage);
    model.addAttribute("pageSize", objectDtoList.getSize());
    model.addAttribute("max", maxPage);
    return "library";
  }

  @GetMapping("/admin/list")
  public String userManagement(HttpServletRequest http, Model model,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "", name = "email") String email,
      @RequestParam(required = false, defaultValue = "all", name = "status") String statusReq) {
    Boolean status = Objects.equals(statusReq, "all") ? null : Boolean.valueOf(statusReq);
    Page<User> userList = userService.getListUser(http, email, status, PageRequest.of(page, 10));
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
}
