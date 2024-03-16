package com.group3.ezquiz.controller;

import com.group3.ezquiz.payload.LibraryReqParam;
import com.group3.ezquiz.payload.LibraryResponse;
import com.group3.ezquiz.payload.ObjectDto;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
          @Valid @ModelAttribute LibraryReqParam libraryDto,
          BindingResult bindingResult,
          Model model, RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      for (ObjectError error : bindingResult.getAllErrors()) {
        FieldError fieldError = (FieldError) error;
        if (fieldError.getField().equals("sort")) {
          libraryDto.setSort("latest");
        }
        if (fieldError.getField().equals("page")) {
          libraryDto.setPage(1);
        }
        if (fieldError.getField().equals("size")) {
          libraryDto.setSize(3);
        }
      }
      redirectAttributes.addAllAttributes(libraryDto.getAttrMap());
      return "redirect:/library";
    }

    LibraryResponse library = userService.getQuizAndClassroomByTeacher(
            request, libraryDto);
    List<ObjectDto> objectDtoList = library.getObjectDtoList();

    if (objectDtoList == null && library.getExceedMaxPage() == true) {
      // case when the current page exceed the max page number
      if (library.getMaxPage() > 0) {
        libraryDto.setPage(library.getMaxPage());
        redirectAttributes.addAllAttributes(libraryDto.getAttrMap());
        return "redirect:/library";
      } else {

      }
    }

    model.addAttribute("object", objectDtoList);
    model.addAttribute("page", libraryDto);
    model.addAttribute("max", library.getMaxPage());
    return "library";
  }

}
