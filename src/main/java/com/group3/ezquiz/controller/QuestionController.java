package com.group3.ezquiz.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.payload.QuestionDto;
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

    @GetMapping
    public String listQuestion(
            HttpServletRequest http,
            Model model,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "", name = "searchTerm") String searchTerm) {
        Page<Question> questionList = questionService.listAll(http, searchTerm, PageRequest.of(page, 5));
        model.addAttribute("questions", questionList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionList.getTotalPages());
        model.addAttribute("search", searchTerm);
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
        questionService.createNewQuestion(request, dto, params);
        return "redirect:/questions";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Question question = questionRepo.findQuestionByQuestionId(id);

        model.addAttribute("question", question);

        return "question/question-editing";
    }

    @PostMapping("/edit/{id}")
    public String updateQuestion(@PathVariable Long id, @ModelAttribute("question") Question updatedQuestion) {
        questionService.updateQuestion(id, updatedQuestion);
        return "redirect:/questions"; // Redirect to the question list page
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        Question question = questionRepo.findById(id).orElse(null);

        if (question != null) {
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
