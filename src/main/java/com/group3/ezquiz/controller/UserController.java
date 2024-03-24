package com.group3.ezquiz.controller;

import com.group3.ezquiz.payload.ObjectDto;
import com.group3.ezquiz.payload.LibraryReqParam;
import com.group3.ezquiz.payload.LibraryResponse;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
      @Valid @ModelAttribute LibraryReqParam params,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes, Model model) {

    if (bindingResult.hasErrors()) {
      params.handleWhenError(bindingResult);
      redirectAttributes.addAllAttributes(params.getAttrMap());
      return "redirect:/library";
    }

    LibraryResponse library = userService.getQuizAndClassroomByTeacher(
        request, params);
    List<ObjectDto> objectDtoList = library.getObjectDtoList();

    if (objectDtoList == null && library.getExceedMaxPage() == true) {
      // case when the current page exceed the max page number
      if (library.getTotalPages() > 0) {
        params.setPage(library.getTotalPages());
        redirectAttributes.addAllAttributes(params.getAttrMap());
        return "redirect:/library";
      } else {

      }
    }

    model.addAttribute("path", "/library");
    model.addAttribute("library", library);
    model.addAttribute("params", params);
    return "library";
  }

}
