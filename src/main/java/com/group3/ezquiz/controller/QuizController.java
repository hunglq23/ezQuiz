package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.QuizAssigning;
import com.group3.ezquiz.payload.AssignedQuizDto;
import com.group3.ezquiz.payload.ExcelFileDto;
import com.group3.ezquiz.payload.LibraryReqParam;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.payload.quiz.QuizToLearner;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.IUserService;
import com.group3.ezquiz.payload.quiz.QuizResult;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

  private final String TEACHER_AUTHORITY = "hasRole('ROLE_TEACHER')";
  private final String LEARNER_AUTHORITY = "hasRole('ROLE_LEARNER')";

  private final IQuizService quizService;
  private final IUserService userService;

  @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_LEARNER')")
  @GetMapping("/{id}")
  public String getQuizDetails(
      HttpServletRequest request,
      @PathVariable UUID id,
      Model model) {

    Quiz quiz = quizService.getQuizById(id);
    model.addAttribute("quiz", quiz);
    model.addAttribute("canEdit", userService.isCreator(request, quiz.getCreator()));
    return "quiz/quiz-details";
  }

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
  @PostMapping("{id}/import")
  public String importData(HttpServletRequest request,
      @PathVariable UUID id,
      @ModelAttribute ExcelFileDto fileDto,
      Model model) throws BindException {
    Quiz quiz = quizService.getQuizByRequestAndID(request, id);
    List<Question> errorQuestions = quizService.importQuizDataFromExcel(request,
        fileDto.getExcelFile(), id);
    model.addAttribute("quiz", quiz);
    if (errorQuestions.size() > 0) {
      model.addAttribute("errorQuestions", errorQuestions);
    }
    return "redirect:/quiz/" + id + "/edit";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
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
  @GetMapping("/{id}/edit-question")
  public String editQuestion(
      HttpServletRequest request,
      @PathVariable("id") UUID quizId,
      @RequestParam("questionId") Long questionId,
      @RequestParam("type") String type,
      Model model) {

    Quiz quiz = quizService.getQuizByRequestAndID(request, quizId);

    Question question = quizService.getQuestionByIdAndQuiz(questionId, quiz);

    model.addAttribute("quiz", quiz);
    model.addAttribute("question", question);
    model.addAttribute("type", type);
    return "question/question-editting";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @PostMapping("/{id}/edit-question")
  public ResponseEntity<?> submitQuestionEditingInQuiz(
      HttpServletRequest request,
      @PathVariable UUID id,
      @RequestParam Long questionId,
      @RequestParam String type,
      @RequestParam("qText") String questionText,
      @RequestParam Map<String, String> params) {

    Quiz quiz = quizService.getQuizByRequestAndID(request, id);
    Quiz saved = quizService.handleQuestionEditingInQuiz(quiz, questionId, type, questionText, params);
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
  @PostMapping("/{id}/delete-question")
  public String deleteQuestion(
      @PathVariable UUID id,
      @RequestParam Long questionId) {
    quizService.deleteQuestionById(id, questionId);
    return "redirect:/quiz/" + id + "/edit";
  }

  @PreAuthorize(LEARNER_AUTHORITY)
  @GetMapping("/{id}/play")
  public String takeQuizByLearner(
      HttpServletRequest request,
      @PathVariable UUID id,
      Model model) {

    QuizToLearner toLearner = quizService.getQuizByLearnerForTaking(request, id);
    model.addAttribute("quiz", toLearner);
    return "quiz/quiz-taking";
  }

  @PreAuthorize(LEARNER_AUTHORITY)
  @PostMapping("/{quizId}/select-answer")
  public ResponseEntity<?> checkQuestionAnswers(
      HttpServletRequest request,
      @PathVariable UUID quizId,
      @RequestParam Long attemptId,
      @RequestParam Long answerId) {

    return quizService.handleAnswerSelected(request, quizId, attemptId, answerId);
  }

  @PreAuthorize(LEARNER_AUTHORITY)
  @PostMapping("/{quizId}/check-answer")
  public ResponseEntity<?> checkQuestionAnswers(
      HttpServletRequest request,
      @PathVariable UUID quizId,
      @RequestParam Long attemptId,
      @RequestParam Long questId,
      @RequestParam String questIndex,
      @RequestParam Map<String, String> params) {

    params.remove("attemptId");
    params.remove("questId");
    params.remove("questIndex");

    return quizService.handleAnswersChecking(request, quizId, attemptId, questId, questIndex, params);
  }

  @PreAuthorize(LEARNER_AUTHORITY)
  @PostMapping("/{quizId}/finish")
  public String finishQuizAttempt(
      HttpServletRequest request,
      @PathVariable UUID quizId,
      @RequestParam Long attemptId) {

    quizService.handleFinishQuizAttempt(request, quizId, attemptId);
    return "redirect:/quiz/" + quizId + "/result";
  }

  @PreAuthorize(LEARNER_AUTHORITY)
  @GetMapping("/{quizId}/result")
  public String viewQuizResultByLearner(
      HttpServletRequest request,
      @PathVariable UUID quizId, Model model) {

    QuizResult quiz = quizService.findLastFinishAttemptResult(request, quizId);

    model.addAttribute("quiz", quiz);
    return "quiz/quiz-result";
  }

  /**
   * Those methods below are not ok
   * need to be refactored
   */
  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("/available-list")
  public String showAvailableQuizList(
      @Valid @ModelAttribute LibraryReqParam params,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model) {

    if (bindingResult.hasErrors()) {
      // if any invalid request params, set that param to default value
      params.handleWhenError(bindingResult);
      redirectAttributes.addAllAttributes(params.getAttrMap());
      return "redirect:/quiz/available-list";
    }
    Page<QuizDto> quizList = quizService.getAvailableQuizList(params);
    model.addAttribute("quizList", quizList);
    model.addAttribute("params", params);
    model.addAttribute("max", quizList.getTotalPages());
    return "quiz/quiz-list";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @PostMapping("/{id}/assign")
  public String assignQuiz(HttpServletRequest request,
      @PathVariable("id") UUID quizId,
      @ModelAttribute("assignedQuizDto") AssignedQuizDto assignedQuizDTO,
      RedirectAttributes redirectAttributes) {

    // try {
    quizService.assignQuiz(request, quizId, assignedQuizDTO);
    redirectAttributes.addFlashAttribute("successMessage", "Homework assigned successfully");
    // } catch (Exception e) {
    // String errorMessage = "Failed to assign homework: " + e.getMessage();
    // redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
    // }

    return "redirect:/quiz/" + quizId + "/edit";
  }

  @GetMapping("/assigned-quizzes")
  public String getAsignedQuizList(HttpServletRequest request, Model model) {
    List<QuizAssigning> assignedQuizList = new ArrayList<>();

    // if (user.getRole().toString().equalsIgnoreCase("TEACHER")) {
    // assignedQuizList = quizAssignService.getAssignedQuizForTeacher(user.getId());
    // } else if (user.getRole().toString().equalsIgnoreCase("LEARNER")) {
    // assignedQuizList =
    // quizAssignService.getAssignedQuizzesForLearner(user.getId());
    // }

    model.addAttribute("assignedQuizList", assignedQuizList);
    // model.addAttribute("user", user);
    return "assign-quiz/assigned-quiz-list";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("assigned/{id}/detail")
  public String getHomeworkDetail(HttpServletRequest request, @PathVariable Long id, Model model) {
    // QuizAssigning assignedQuiz = quizAssignService.findById(id);
    // model.addAttribute("assignedQuiz", assignedQuiz);
    return "assign-quiz/assigned-quiz-detail";
  }

  @GetMapping("/remove/{id}")
  public String removeAssignedQuiz(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    // quizAssignService.removeAssignedQuiz(id);
    // redirectAttributes.addFlashAttribute("successMessage", "Assigned quiz removed
    // successfully");
    return "redirect:/quiz/assigned-quizzes";
  }
}
