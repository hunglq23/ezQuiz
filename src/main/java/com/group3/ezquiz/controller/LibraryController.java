package com.group3.ezquiz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group3.ezquiz.payload.CodeFormDto;
import com.group3.ezquiz.payload.LibraryReqParam;
import com.group3.ezquiz.payload.LibraryResponse;
import com.group3.ezquiz.payload.ObjectDto;
import com.group3.ezquiz.payload.QuizReqParam;
import com.group3.ezquiz.payload.classroom.ClassroomDto;
import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.service.ILibraryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/library")
public class LibraryController {

  @Autowired
  private ILibraryService libraryService;

  private final String TEACHER_AUTHORITY = "hasRole('ROLE_TEACHER')";
  private final String LEARNER_AUTHORITY = "hasRole('ROLE_LEARNER')";

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/my-library")
  public String getLibraryPage(
      HttpServletRequest request,
      @Valid @ModelAttribute LibraryReqParam params,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes, Model model) {

    final String PATH = "/library/my-library";
    final String DO_REDIRECT = "redirect:" + PATH;

    if (bindingResult.hasErrors()) {
      params.handleWhenError(bindingResult);
      redirectAttributes.addAllAttributes(params.getAttrMap());
      return DO_REDIRECT;
    }

    LibraryResponse library = libraryService.getCreatedQuizAndClassroom(request, params);
    List<ObjectDto> objectDtoList = library.getObjectDtoList();

    if (objectDtoList == null && library.getExceedMaxPage() == true) {
      // case when the current page exceed the max page number
      if (library.getTotalPages() > 0) {
        params.setPage(library.getTotalPages());
        redirectAttributes.addAllAttributes(params.getAttrMap());
        return DO_REDIRECT;
      }
    }
    model.addAttribute("path", PATH);
    model.addAttribute("library", library);
    model.addAttribute("params", params);
    return "library";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/my-quiz")
  public String showCreatedQuizList(
      HttpServletRequest request,
      @Valid @ModelAttribute QuizReqParam params,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    final String PATH = "/library/my-quiz";
    final String DO_REDIRECT = "redirect:" + PATH;

    if (bindingResult.hasErrors()) {
      // if any invalid request params, set that param to default value
      params.handleWhenError(bindingResult);
      redirectAttributes.addAllAttributes(params.getAttrMap());
      return DO_REDIRECT;
    }

    Page<QuizDto> page = libraryService.getMyQuizInLibrary(request, params);
    if (page.getTotalPages() > 0 && page.getTotalPages() < params.getPage()) {
      params.setPage(page.getTotalPages());
      redirectAttributes.addAllAttributes(params.getAttrMap());
      return DO_REDIRECT;
    }

    model.addAttribute("path", PATH);
    model.addAttribute("page", page);
    model.addAttribute("params", params);
    return "quiz/quiz-list";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/my-classroom")
  public String showCreatedClassrooms(
      HttpServletRequest request,
      @Valid @ModelAttribute LibraryReqParam params,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    final String PATH = "/library/my-classroom";
    final String DO_REDIRECT = "redirect:" + PATH;

    if (bindingResult.hasErrors()) {
      // if any invalid request params, set that param to default value
      params.handleWhenError(bindingResult);
      redirectAttributes.addAllAttributes(params.getAttrMap());
      return DO_REDIRECT;
    }

    Page<ClassroomDto> page = libraryService.getMyClassroomInLibrary(request, params);
    if (page.getTotalPages() > 0 && page.getTotalPages() < params.getPage()) {
      params.setPage(page.getTotalPages());
      redirectAttributes.addAllAttributes(params.getAttrMap());
      return DO_REDIRECT;
    }

    model.addAttribute("path", PATH);
    model.addAttribute("page", page);
    model.addAttribute("params", params);
    return "classroom/classroom-list";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/my-classroom/new")
  public String newClassroom() {
    return "redirect:/library/my-classroom?page=1&sort=latest&size=3&new";
  }

  @PreAuthorize(LEARNER_AUTHORITY)
  @GetMapping("/joined-classrooms")
  public String joinedClassrooms(
      HttpServletRequest request,
      @Valid @ModelAttribute LibraryReqParam params,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {
    final String PATH = "/library/joined-classrooms";
    final String DO_REDIRECT = "redirect:" + PATH;

    if (bindingResult.hasErrors()) {
      // if any invalid request params, set that param to default value
      params.handleWhenError(bindingResult);
      redirectAttributes.addAllAttributes(params.getAttrMap());
      return DO_REDIRECT;
    }

    Page<ClassroomDto> page = libraryService.getJoinedClassrooms(request, params);

    if (page.getTotalPages() < params.getPage() && page.getTotalPages() > 0) {
      params.setPage(page.getTotalPages());
      redirectAttributes.addAllAttributes(params.getAttrMap());
      return DO_REDIRECT;
    }

    model.addAttribute("path", PATH);
    model.addAttribute("page", page);
    model.addAttribute("params", params);
    model.addAttribute("classroom", new CodeFormDto());
    return "classroom/classroom-list";
  }

}
