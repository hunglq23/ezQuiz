package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.QuizDto;
import com.group3.ezquiz.service.IQuizService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ROLE_TEACHER')")
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final IQuizService quizService;

    @GetMapping("")
    public String showQuizList(Model model) {
        List<Quiz> quizList = quizService.listAll();
        model.addAttribute("listQuiz", quizList);
        return "quiz/quiz";
    }

    @GetMapping("create")
    public String showCreateQuizForm(Model model) {
        model.addAttribute("quiz", new QuizDto());
        return "quiz/quiz-creating";
    }

    @PostMapping("create")
    public String createQuiz(HttpServletRequest http, QuizDto quizDto) {
        // process the form data
        quizService.createQuiz(http, quizDto);
        // redirect to the quiz list page after creating a new quiz
        return "redirect:/quiz";
    }

    @GetMapping("edit/{id}")
    public String showQuizDetail(@PathVariable("id") Integer id, Model model) {
        Quiz existedQuiz = quizService.findQuizById(id);
        model.addAttribute("quiz", existedQuiz);
        // existedQuiz.ifPresent(quiz -> model.addAttribute("quiz", quiz));
        return "quiz/quiz-detail";
    }

    @PostMapping("update/{id}")
    public String updateQuiz(
            HttpServletRequest http,
            @PathVariable("id") Integer id,
            @ModelAttribute("quiz") QuizDto updateQuiz) {

        quizService.updateQuiz(http, id, updateQuiz);

        return "redirect:/quiz";
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Integer id) {
        quizService.deleteQuiz(id);
        return "redirect:/quiz";
    }
}
