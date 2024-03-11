package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.service.IQuizService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

  private final String TEACHER_AUTHORITY = "hasRole('ROLE_TEACHER')";
  // private final String LEARNER_AUTHORITY = "hasRole('ROLE_LEARNER')";

  private final IQuizService quizService;

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/new")
  public String handleQuizCreatingRequest(HttpServletRequest request) {
    Quiz quiz = quizService.getDraftQuiz(request);
    return "redirect:" + "/quiz" + '/' + quiz.getId() + "/edit";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/{id}/edit")
  public String getQuizEditPage(
      HttpServletRequest request,
      @PathVariable UUID id,
      Model model) {
    Quiz quiz = quizService.getQuizByRequestAndID(request, id);
    model.addAttribute("quiz", quiz);
    return "quiz/quiz-editing";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/{id}/edit/create-question")
  public String getQuestionCreatingForm(
      HttpServletRequest request,
      @PathVariable UUID id,
      @RequestParam(required = false, defaultValue = "") String type,
      Model model) {
    Quiz quiz = quizService.getQuizByRequestAndID(request, id);
    if (!Quiz.AVAILABLE_TYPES.contains(type)) {
      return "redirect:/quiz/" + id + "/edit/create-question?type=single-choice";
    }
    model.addAttribute("quiz", quiz);
    model.addAttribute("qType", type);
    return "question/question-creating";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @PostMapping("/{id}/add-question")
  public ResponseEntity<?> submitQuestionCreatingInQuiz(
      HttpServletRequest request,
      @PathVariable UUID id,
      @RequestParam String type,
      @RequestParam(name = "qText") String questionText,
      @RequestParam Map<String, String> params) {

    Quiz quiz = quizService.getQuizByRequestAndID(request, id);
    Quiz saved = quizService.handleQuestionCreatingInQuiz(quiz, type, questionText, params);
    if (saved == null) {
      return ResponseEntity.badRequest().body("Failed!");
    }
    return ResponseEntity.ok(
        MessageResponse.builder()
            .message(questionText)
            .timestamp(LocalDateTime.now())
            .build());
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @PutMapping("/{id}/edit")
  public ResponseEntity<?> updateQuizDetails(
      HttpServletRequest request,
      @PathVariable UUID id,
      @Valid @ModelAttribute QuizDetailsDto dto) throws BindException {

    return quizService.handleQuizUpdatingRequest(request, id, dto);
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/my-quiz")
  public String showQuizList(
      HttpServletRequest request,
      @RequestParam(required = false, value = "sort") Optional<String> sortOrder,
      @RequestParam(required = false, value = "draft") Boolean isDraft,
      @RequestParam(required = false, value = "page") Optional<Integer> page,
      @RequestParam(required = false, value = "size") Optional<Integer> size,
      Model model) {

    String sort = sortOrder.orElse("latest");
    Integer currentPage = page.orElse(1);
    Integer pageSize = size.orElse(3);

    // String[] availableSortList = { "latest", "oldest" };
    // if (Arrays.asList(availableSortList).contains(sortOrder)) {
    // return "redirect:/quiz/my-quiz?sortOrder=latest";
    // }
    // Boolean isDraft = null;
    // if (!draft.isEmpty()) {
    // try {
    // isDraft = Boolean.parseBoolean(draft);
    // } catch (NumberFormatException e) {
    // // return "redirect:/quiz/my-quiz";
    // }
    // }
    Page<QuizDto> quizPage = quizService.getQuizInLibrary(
        request, sort, isDraft,
        PageRequest.of(currentPage - 1, pageSize));

    int maxPage = quizPage.getTotalPages();
    int curPage = quizPage.getNumber() + 1;

    model.addAttribute("quiz", quizPage);
    model.addAttribute("isDraft", isDraft);
    model.addAttribute("sort", sortOrder);
    model.addAttribute("currentPage", curPage > maxPage ? maxPage : curPage);
    model.addAttribute("pageSize", quizPage.getSize());
    model.addAttribute("max", maxPage);
    return "quiz/quiz-list";
  }
}
