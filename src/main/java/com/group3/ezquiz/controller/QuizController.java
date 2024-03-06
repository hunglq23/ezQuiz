package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
import com.group3.ezquiz.service.IQuizService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
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

}
