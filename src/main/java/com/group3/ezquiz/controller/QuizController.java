// package com.group3.ezquiz.controller;

// import com.group3.ezquiz.model.Quiz;
// import com.group3.ezquiz.payload.QuizDto;
// import com.group3.ezquiz.service.IQuizService;
// import com.group3.ezquiz.service.impl.QuizServiceImpl;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import java.util.List;

// @Controller
// @PreAuthorize("hasRole('ROLE_TEACHER')")
// @RequiredArgsConstructor
// @RequestMapping("/quiz")
// public class QuizController {

// private final IQuizService quizService;

// @GetMapping("")
// public String showQuizList(
// HttpServletRequest http,
// Model model,
// @RequestParam(defaultValue = "0") Integer page,
// @RequestParam(required = false, defaultValue = "", name = "searchTerm")
// String searchTerm) {
// Page<Quiz> quizList = quizService.listAll(http, searchTerm,
// PageRequest.of(page, 5));
// model.addAttribute("quizList", quizList);
// model.addAttribute("items", quizList.getContent());
// model.addAttribute("currentPage", page);
// model.addAttribute("totalPages", quizList.getTotalPages());
// model.addAttribute("search", searchTerm);
// // List<Quiz> quizList = quizService.listAll();
// // model.addAttribute("listQuiz", quizList);
// return "quiz/quiz";
// }

// @GetMapping("create")
// public String showCreateQuizForm(Model model) {
// model.addAttribute("quiz", new QuizDto());
// return "quiz/quiz-creating";
// }

// // @PostMapping("create")
// // public String createQuiz(HttpServletRequest http, QuizDto quizDto, Model
// model) {
// // // process the form data
// // quizService.createQuiz(http, quizDto);
// // // redirect to the quiz list page after creating a new quiz
// // return "redirect:/quiz";
// // }

// @PostMapping("/create")
// public ResponseEntity<String> createQuiz(HttpServletRequest request, QuizDto
// quizDto) {
// try {
// quizService.createQuiz(request, quizDto);
// return ResponseEntity.ok("Create Quiz Successfully!");
// } catch (IllegalArgumentException e) {
// return ResponseEntity.status(HttpStatus.BAD_REQUEST).
// body("{\"errorMessage\": \"" + e.getMessage() + "\"}");
// }
// }

// @GetMapping("edit/{id}")
// public String showQuizEditForm(@PathVariable("id") Integer id, Model model) {
// Quiz existedQuiz = quizService.findQuizById(id);
// model.addAttribute("quiz", existedQuiz);
// return "quiz/quiz-editing";
// }

// @GetMapping("/detail/{id}")
// public String showQuizDetail(@PathVariable("id") Integer id, Model model) {
// Quiz existedQuiz = quizService.findQuizById(id);
// model.addAttribute("quiz", existedQuiz);
// return "quiz/quiz-detail";
// }

// @PostMapping("update/{id}")
// public String updateQuiz(
// HttpServletRequest http,
// @PathVariable("id") Integer id,
// @ModelAttribute("quiz") QuizDto updateQuiz,
// RedirectAttributes redirectAttributes) {
// quizService.updateQuiz(http, id, updateQuiz);
// redirectAttributes.addAttribute("id", id);
// return "redirect:/quiz/detail/{id}";
// }

// @GetMapping("/delete/{id}")
// public String deleteQuestion(@PathVariable Integer id) {
// quizService.deleteQuiz(id);
// return "redirect:/quiz";
// }

// @GetMapping("/toggle/{id}")
// public String toggleQuestionStatus(@PathVariable Integer id) {
// quizService.toggleQuizStatus(id);
// return "redirect:/quiz";
// }
// }
