package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.QuizAssigning;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.AssignedQuizDto;
import com.group3.ezquiz.payload.ExcelFileDto;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.quiz.QuizDetailsDto;
import com.group3.ezquiz.payload.quiz.QuizToLearner;
import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.service.IClassroomService;
import com.group3.ezquiz.service.IQuizService;
import com.group3.ezquiz.service.IUserService;
import com.group3.ezquiz.service.impl.QuestionServiceImpl;
import com.group3.ezquiz.service.impl.QuizAssigningService;
import com.group3.ezquiz.payload.quiz.QuizResult;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

  private final String TEACHER_AUTHORITY = "hasRole('ROLE_TEACHER')";
  private final String LEARNER_AUTHORITY = "hasRole('ROLE_LEARNER')";

  private final IQuizService quizService;
  private final QuizAssigningService quizAssignService;
  private final IUserService userService;
  private final IClassroomService classroomService;

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
    User user = userService.getUserRequesting(request);
    List<Classroom> classrooms = classroomService.findClassroomsByCreatorId(user.getId());
    model.addAttribute("quiz", quiz);
    model.addAttribute("user", user);
    model.addAttribute("classrooms", classrooms);
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

  @PostMapping("/{id}/assign")
  public String assignQuiz(HttpServletRequest request,
      @PathVariable("id") UUID quizId,
      @ModelAttribute("assignedQuizDto") AssignedQuizDto assignedQuizDTO,
      RedirectAttributes redirectAttributes) {

    try {
      quizService.assignQuiz(request, quizId, assignedQuizDTO);
      redirectAttributes.addFlashAttribute("successMessage", "Homework assigned successfully");
    } catch (Exception e) {
      String errorMessage = "Failed to assign homework: " + e.getMessage();
      redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
    }

    return "redirect:/quiz/" + quizId + "/edit";
  }

  @GetMapping("/assigned-quizzes")
  public String getAsignedQuizList(HttpServletRequest request, Model model) {
    User user = userService.getUserRequesting(request);
    List<QuizAssigning> assignedQuizList = new ArrayList<>();

    if (user.getRole().toString().equalsIgnoreCase("TEACHER")) {
      assignedQuizList = quizAssignService.getAssignedQuizForTeacher(user.getId());
    } else if (user.getRole().toString().equalsIgnoreCase("LEARNER")) {
      assignedQuizList = quizAssignService.getAssignedQuizzesForLearner(user.getId());
    }

    model.addAttribute("assignedQuizList", assignedQuizList);
    model.addAttribute("user", user);
    return "assign-quiz/assigned-quiz-list";
  }

  @PreAuthorize(TEACHER_AUTHORITY)
  @GetMapping("assigned/{id}/detail")
  public String getHomeworkDetail(HttpServletRequest request, @PathVariable Long id, Model model) {
    User teacher = userService.getUserRequesting(request);
    QuizAssigning assignedQuiz = quizAssignService.findById(id);
    model.addAttribute("user", teacher);
    model.addAttribute("assignedQuiz", assignedQuiz);
    return "assign-quiz/assigned-quiz-detail";
  }

  @GetMapping("/remove/{id}")
  public String removeAssignedQuiz(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    quizAssignService.removeAssignedQuiz(id);
    redirectAttributes.addFlashAttribute("successMessage", "Assigned quiz removed successfully");
    return "redirect:/quiz/assigned-quizzes";
  }

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
      @RequestParam("questionId") Long questionId,
      @RequestParam String type,
      @RequestParam(name = "qText") String questionText,
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
  public String deleteQuestion(@PathVariable UUID id, @RequestParam("questionId") Long questionId) {
    // Call the service to delete the question
    quizService.deleteQuestionById(id, questionId);

    // Redirect back to the edit page for the quiz
    return "redirect:/quiz/" + id + "/edit";
  }

}
