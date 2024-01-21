package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.QuizDto;
import com.group3.ezquiz.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@PreAuthorize("hasRole('TEACHER')")
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("")
    public String showQuizList(Model model){
        List<Quiz> quizList = quizService.listAll();
        model.addAttribute("listQuiz", quizList);
        return "quiz/quiz";
    }

    @GetMapping("create")
    public String showCreateQuizForm(Model model){
        model.addAttribute("quiz", new QuizDto());
        return "quiz/quiz-creating";
    }

    @PostMapping("create")
    public String createQuiz(HttpServletRequest http, QuizDto quizDto){
        quizService.createQuiz(http, quizDto);
        return "redirect:/quiz";
    }

}
