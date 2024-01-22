package com.group3.ezquiz.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.payload.QuestionDto;
import com.group3.ezquiz.repository.OptionRepo;
import com.group3.ezquiz.repository.QuestionRepo;
import com.group3.ezquiz.service.impl.QuestionServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_TEACHER')")
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionServiceImpl questionService;
    private final QuestionRepo questionRepo;
    private final OptionRepo optionRepo;

    @GetMapping
    public String listQuestions(Model model, @RequestParam(name = "searchText", required = false) String searchText) {
        List<Question> questions;

        if (searchText != null && !searchText.isEmpty()) {
            questions = questionService.searchQuestionsByText(searchText);
        } else {
            questions = questionService.getAllQuestions();
        }

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
            HttpServletRequest request,
            @ModelAttribute QuestionDto dto,
            @RequestParam Map<String, String> params,
            Model model) {

        // Process the form data
        questionService.createNewQuestion(request, dto, params);

        // Redirect to the questions page after creating the question
        return "redirect:/questions";
    }

    @PostMapping("/edit/{id}")
    public String updateQuestion(@PathVariable Long id,
            @ModelAttribute("question") Question updatedQuestion) {
        questionService.updateQuestion(id, updatedQuestion);
        return "redirect:/questions";
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        Question question = questionRepo.findById(id).orElse(null);

        if (question != null) {
            // Delete associated options first
            optionRepo.deleteAllByQuestion(question.getQuestionId());

            // Then delete the question
            questionRepo.deleteById(id);
        }

        return "redirect:/questions";
    }

    @GetMapping("/toggle/{id}")
    public String toggleQuestionStatus(@PathVariable Long id) {
        questionService.toggleQuestionStatus(id);
        return "redirect:/questions";
    }

}
