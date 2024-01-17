package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.QuizRequest;
import com.group3.ezquiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/quiz")
    public String showQuizList(Model model){
        List<Quiz> quizList = quizService.listAll();
        model.addAttribute("listQuiz", quizList);
        return "quiz";
    }

    @GetMapping("/quiz/create")
    public String showCreateQuizForm(Model model){
        model.addAttribute("quiz", new QuizRequest());
        return "create_quiz";
    }
}
