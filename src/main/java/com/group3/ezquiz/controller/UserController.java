package com.group3.ezquiz.controller;

import com.group3.ezquiz.payload.ObjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

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
        request, sort, PageRequest.of(currentPage - 1, pageSize));
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

}
