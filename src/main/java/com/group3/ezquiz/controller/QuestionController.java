package com.group3.ezquiz.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import java.util.Map;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_TEACHER')")
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionServiceImpl questionService;
    private final QuestionRepo questionRepo;
    private final OptionRepo optionRepo;

    @GetMapping("")
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
        // List<Quiz> quizList = quizService.listAll();
        // model.addAttribute("listQuiz", quizList);
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
        // questionService.deleteQuestionById(id);

        return "redirect:/questions";
    }

    @GetMapping("/toggle/{id}")
    public String toggleQuestionStatus(@PathVariable Long id) {
        questionService.toggleQuestionStatus(id);
        return "redirect:/questions";
    }

    @GetMapping("/create2")
    public String getQuestionCreatingPage(Model model) {
        model.addAttribute("question", new QuestionDto());
        return "question/question-creating2";
    }

    @GetMapping("/2")
    public String getQuestionList(
            HttpServletRequest http,
            Model model,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "", name = "searchTerm") String searchTerm) {
        Page<Question> questionList = questionService.listAll(http, searchTerm, PageRequest.of(page, 5));
        model.addAttribute("questions", questionList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionList.getTotalPages());
        model.addAttribute("search", searchTerm);
        // List<Quiz> quizList = quizService.listAll();
        // model.addAttribute("listQuiz", quizList);
        return "question/question-list";
    }

}
