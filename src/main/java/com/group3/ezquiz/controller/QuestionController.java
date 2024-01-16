package com.group3.ezquiz.controller;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final IQuestionService questionService;

    @Autowired
    public QuestionController(IQuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/list")
    public String listQuestions(Model model) {
        List<Question> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "question/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("question", new Question());
        return "question/create";
    }

    @PostMapping("/create")
    public String createQuestion(@ModelAttribute("question") Question question) {
        questionService.createQuestion(question);
        return "redirect:/questions/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            Question question = questionService.getQuestionById(id)
                    .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
            model.addAttribute("question", question);
            return "question/edit";
        } catch (ChangeSetPersister.NotFoundException e) {
            return "error/404"; // Create an HTML template for the 404 error page
        }
    }

    @PostMapping("/edit/{id}")
    public String updateQuestion(@PathVariable Integer id, @ModelAttribute("question") Question updatedQuestion) {
        try {
            questionService.updateQuestion(id, updatedQuestion);
            return "redirect:/questions/list";
        } catch (ChangeSetPersister.NotFoundException e) {
            return "error/404"; // Create an HTML template for the 404 error page
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Integer id) {
        questionService.deleteQuestion(id);
        return "redirect:/questions/list";
    }
}
