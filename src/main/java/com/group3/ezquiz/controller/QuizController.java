package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.ExcelFileDto;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.quiz.*;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.impl.QuestionServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

  private final static Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);

  private final String TEACHER_AUTHORITY = "hasRole('ROLE_TEACHER')";
  private final String LEARNER_AUTHORITY = "hasRole('ROLE_LEARNER')";

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

  @PostMapping("{id}/import")
  public String importData(HttpServletRequest request,
      @PathVariable UUID id, @ModelAttribute ExcelFileDto fileDto, Model model)
      throws BindException {
    Quiz quiz = quizService.getQuizByRequestAndID(request, id);
    List<Question> errorQuestions = quizService.importQuizDataFromExcel(request,
        fileDto.getExcelFile(), id);
    model.addAttribute("quiz", quiz);
    if (errorQuestions.size() > 0) {
      model.addAttribute("errorQuestions", errorQuestions);
      for (Question errorQuestion : errorQuestions) {
        log.info(errorQuestion.getText());
      }
    }
    return "quiz/quiz-editing";
  }

  @GetMapping("/download")
  public ResponseEntity<InputStreamResource> downloadTemplate() throws IOException {
    ClassPathResource resource = new ClassPathResource("Template.xlsx");
    InputStream inputStream = resource.getInputStream();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Template.xlsx");

    return ResponseEntity
        .ok()
        .headers(headers)
        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
        .body(new InputStreamResource(inputStream));
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/my-quiz")
  public String showQuizList(
          HttpServletRequest request,
          @Valid @ModelAttribute QuizReqParam quizReq,
          BindingResult bindingResult,
          RedirectAttributes redirectAttributes,
          Model model) {
    if(bindingResult.hasErrors()){
      for(ObjectError error: bindingResult.getAllErrors()){
        FieldError fieldError = (FieldError) error;
        if(fieldError.getField().equals("sort")){
          quizReq.setSort("latest");
        }
        if(fieldError.getField().equals("draft")) {
          quizReq.setDraft("");
        }
        if (fieldError.getField().equals("page")) {
          quizReq.setPage(1);
        }
        if (fieldError.getField().equals("size")) {
          quizReq.setSize(3);
        }
      }
      HashMap<String, String> attrMap = quizReq.getAttrMap();
      redirectAttributes.addAllAttributes(attrMap);
      return "redirect:/quiz/quiz-list";
    }
    Page<QuizDto> quiz = quizService.getQuizInLibrary(request, quizReq);
    int maxPage = quiz.getTotalPages();

    model.addAttribute("quizList", quiz);
    model.addAttribute("page", quizReq);
    model.addAttribute("max", maxPage);
    model.addAttribute("totalItemNumber", quiz.getTotalElements());
    return "quiz/quiz-list";
  }

  @GetMapping("/delete/{id}")
  public String deleteQuestion(@PathVariable UUID id) {
    quizService.deleteQuiz(id);
    return "redirect:/quiz/my-quiz";
  }

  @PreAuthorize(LEARNER_AUTHORITY)
  @GetMapping("/{id}/play")
  public String takeQuizByLearner(
      @PathVariable UUID id,
      Model model) {
    QuizToLearner toLearner = quizService.getQuizByLearnerForTaking(id);
    model.addAttribute("quiz", toLearner);
    return "quiz/quiz-taking";
  }

  @PreAuthorize(LEARNER_AUTHORITY)
  @PostMapping("/{quizId}/check-answer")
  public ResponseEntity<?> checkQuestionAnswers(
      @PathVariable UUID quizId,
      @RequestParam Long questId,
      @RequestParam String questIndex,
      @RequestParam Map<String, String> params) {

    params.remove("questId");
    params.remove("questIndex");

    return quizService.handleAnswersChecking(quizId, questId, questIndex, params);
  }

}
