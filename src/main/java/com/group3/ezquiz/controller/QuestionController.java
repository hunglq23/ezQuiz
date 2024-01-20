package com.group3.ezquiz.controller;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.payload.QuestionDto;
import com.group3.ezquiz.service.impl.QuestionServiceImpl;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionServiceImpl questionService;

    @GetMapping
    public String listQuestions(Model model) {
        List<Question> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "question/question";
    }

    @GetMapping("/create")
    public String showCreateQuestionForm(Model model) {
        model.addAttribute("question", new Question());
        return "question/question-creating";
    }

    @PostMapping("/create")
    public String submitQuestionCreatingForm(
            @ModelAttribute QuestionDto dto,
            @RequestParam Map<String, String> params,
            Model model) {

        // Process the form data
        questionService.createNewQuestion(dto, params);

        // Redirect to the questions page after creating the question
        return "redirect:/questions";
    }

    @PostMapping("/edit/{id}")
    public String updateQuestion(@PathVariable Long id,
            @ModelAttribute("question") Question updatedQuestion) {
        questionService.updateQuestion(id, updatedQuestion);
        System.out.println("sca");
        return "redirect:/questions";
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "redirect:/questions";
    }

    @GetMapping("/toggle/{id}")
    public String toggleQuestionStatus(@PathVariable Long id) {
        questionService.toggleQuestionStatus(id);
        return "redirect:/questions";
    }

}
