package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.service.impl.QuizServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class QuizController {
    @Autowired
    private QuizServiceImpl quizService;

    @GetMapping("/quiz")
    public String showQuizList(Model model) {
        List<Quiz> quizList = quizService.listAll();
        model.addAttribute("listQuiz", quizList);
        return "quiz";
    }

    @GetMapping("/add")
    public String showAddQuizForm() {
        return "add_quiz";
    }
}
