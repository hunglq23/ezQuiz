package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.ExcelFileDto;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
import com.group3.ezquiz.payload.quiz.QuizToLearner;
import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.impl.QuestionServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
      @RequestParam(required = false, value = "sort") Optional<String> sortOrder,
      @RequestParam(required = false, value = "draft") Boolean isDraft,
      @RequestParam(required = false, value = "page") Optional<Integer> page,
      @RequestParam(required = false, value = "size") Optional<Integer> size,
      Model model) {
    String sort = sortOrder.orElse("latest");
    Integer currentPage = page.orElse(1);
    Integer pageSize = size.orElse(3);

    String draftParam = isDraft == null ? "" : isDraft.toString();
    if (currentPage < 1)
      return "redirect:/quiz/my-quiz?sort=" + sort +
          "&draft=" + draftParam +
          "&page=1&size=" + pageSize;

    Page<QuizDto> quizPage = quizService.getQuizInLibrary(
        request, sort, isDraft, PageRequest.of(currentPage - 1, pageSize));

    int maxPage = quizPage.getTotalPages();
    if (currentPage > maxPage) {
      return "redirect:/quiz/my-quiz?sort=" + sort +
          "&draft=" + draftParam +
          "&page=" + maxPage +
          "&size=" + pageSize;
    }
    int curPage = quizPage.getNumber() + 1;

    model.addAttribute("quizList", quizPage);
    model.addAttribute("isDraft", isDraft);
    model.addAttribute("sort", sort);
    model.addAttribute("currentPage", curPage > maxPage ? maxPage : curPage);
    model.addAttribute("pageSize", quizPage.getSize());
    model.addAttribute("max", maxPage);
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
