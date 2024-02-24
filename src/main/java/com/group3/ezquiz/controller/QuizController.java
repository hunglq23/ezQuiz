package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.QuizUUID;
import com.group3.ezquiz.payload.ExcelFileDto;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.QuizDetailsDto;
import com.group3.ezquiz.payload.QuizDto;
import com.group3.ezquiz.service.IQuizService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasAnyRole('LEARNER', 'TEACHER')")
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final String LEARNER_AUTHORITY = "hasRole('ROLE_LEARNER')";
    private final String TEACHER_AUTHORITY = "hasRole('ROLE_TEACHER')";

    private final IQuizService quizService;

    // @GetMapping("/search?code=23847da234asf")
    // public String search() {
    // return null;
    // }

    // @PreAuthorize(LEARNER_AUTHORITY)
    // @GetMapping("/assigned-list")
    // public String assigned() {
    // return null;
    // }

    // @PreAuthorize(LEARNER_AUTHORITY)
    // @GetMapping("/taken-list")
    // public String joinedList() {
    // return null;
    // }

    @PreAuthorize(LEARNER_AUTHORITY)
    @GetMapping("/{id}/start/{quiz-taking-id}?exam=0")
    public String takeAQuiz() {
        return null;
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/new")
    public String handleQuizCreatingRequest(HttpServletRequest request) {
        QuizUUID quiz = quizService.saveAndGetDraftQuiz(request);
        return "redirect:" + "/quiz" + '/' + quiz.getId() + "/edit";
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/{id}/edit")
    public String getQuizEditPage(
            HttpServletRequest request,
            @PathVariable UUID id,
            Model model) {
        QuizUUID quiz = quizService.getQuizByRequestAndUUID(request, id);
        model.addAttribute("quiz", quiz);
        return "quiz/quiz-editing";
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
    @GetMapping("/{id}/edit/create-question")
    public String getQuestionCreatingForm(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "") String type,
            @RequestParam String trigger,
            Model model) {
        QuizUUID quiz = quizService.getQuizByRequestAndUUID(request, id);
        if (!QuizUUID.AVAILABLE_TYPES.contains(type)) {
            return "redirect:/quiz/" + id + "/edit/create-question?type=single-choice";
        }
        model.addAttribute("quiz", quiz);
        model.addAttribute("qType", type);
        model.addAttribute("trigger", trigger);
        return "quest/question-creating";
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @PostMapping("/{id}/add-question")
    public String submitQuestionCreatingInQuiz(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestParam String type,
            @RequestParam(name = "qText") String questionText,
            @RequestParam Map<String, String> params) {

        QuizUUID quiz = quizService.getQuizByRequestAndUUID(request, id);
        return quizService.handleQuestionCreatingInQuiz(request, quiz, type, questionText, params);
    }

    // @PreAuthorize(TEACHER_AUTHORITY)
    // @GetMapping("/assignable-list")
    // public String getAssignableQuiz() {
    // return null;
    // }

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("")
    public String showQuizList(
            HttpServletRequest http,
            Model model,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "", name = "searchTerm") String searchTerm) {
        Page<Quiz> quizList = quizService.listAll(http, searchTerm, PageRequest.of(page, 5));
        model.addAttribute("quizList", quizList);
        model.addAttribute("items", quizList.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", quizList.getTotalPages());
        model.addAttribute("search", searchTerm);
        // List<Quiz> quizList = quizService.listAll();
        // model.addAttribute("listQuiz", quizList);
        return "quiz/quiz";
    }

    @GetMapping("/create")
    public String showCreateQuizForm(Model model) {
        model.addAttribute("quiz", new QuizDto());
        return "quiz/quiz-creating";
    }

    @PostMapping("/create")
    public ResponseEntity<String> createQuiz(HttpServletRequest request, QuizDto quizDto) {
        try {
            quizService.createQuiz(request, quizDto);
            return ResponseEntity.ok("Create Quiz Successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"errorMessage\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("edit/{id}")
    public String showQuizEditForm(@PathVariable("id") Integer id, Model model) {
        Quiz existedQuiz = quizService.findQuizById(id);
        model.addAttribute("quiz", existedQuiz);
        return "quiz/quiz-editing";
    }

    @GetMapping("/detail/{id}")
    public String showQuizDetail(@PathVariable("id") Integer id, Model model) {
        Quiz existedQuiz = quizService.findQuizById(id);
        model.addAttribute("quiz", existedQuiz);
        return "quiz/quiz-detail";
    }

    @PostMapping("update/{id}")
    public String updateQuiz(
            HttpServletRequest http,
            @PathVariable("id") Integer id,
            @ModelAttribute("quiz") QuizDto updateQuiz,
            RedirectAttributes redirectAttributes) {
        quizService.updateQuiz(http, id, updateQuiz);
        redirectAttributes.addAttribute("id", id);
        return "redirect:/quiz/detail/{id}";
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Integer id) {
        quizService.deleteQuiz(id);
        return "redirect:/quiz";
    }

    @GetMapping("/toggle/{id}")
    public String toggleQuestionStatus(@PathVariable Integer id) {
        quizService.toggleQuizStatus(id);
        return "redirect:/quiz";
    }

    @PostMapping("{id}/import")
    public ResponseEntity<?> importData(HttpServletRequest request,
            @PathVariable UUID id, @ModelAttribute ExcelFileDto fileDto) throws BindException {
        quizService.importQuizDataFromExcel(request, fileDto.getExcelFile(), id);

        return new ResponseEntity<>(
                new MessageResponse("HI"),
                HttpStatus.OK);
    }
}
