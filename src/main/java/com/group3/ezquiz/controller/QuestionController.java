package com.group3.ezquiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.service.IQuestionService;

import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final IQuestionService questionService;
    private final OptionController optionController; // Inject OptionController

    @Autowired
    public QuestionController(IQuestionService questionService, OptionController optionController) {
        this.questionService = questionService;
        this.optionController = optionController;
    }

    @GetMapping
    public String listQuestions(Model model) {
        List<Question> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "questions";
    }

    @GetMapping("/create")
    public String showCreateForm(Question question, Model model) {
        // Set isActive to true for the newly created question
        question.setActive(true);
        model.addAttribute("question", new Question());
        return "add_question";
    }

    @PostMapping("/create")
    public String createQuestion(Question question, RedirectAttributes redirectAttributes) {
        // Check if options are null before iterating
        List<Option> options = question.getOptions();
        if (options != null) {
            for (Option option : options) {
                option.setQuestion(question);
                option.setAnswer(option.isAnswer());
                option.setContent(option.getContent());
                optionController.createOption(option, question.getQuestionId());
            }
        } else {
            // Handle the case when options are null (e.g., throw an exception, log a
            // message, etc.)
            // Redirect back to the "/questions" page with an error message
            redirectAttributes.addFlashAttribute("error", "Options cannot be null");
            return "redirect:/questions";
        }

        // Your remaining logic for creating a question
        // ...
        questionService.createQuestion(question);
        // Redirect to the "/questions" page after successful creation
        return "redirect:/questions";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            Question question = questionService.getQuestionById(id)
                    .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
            model.addAttribute("question", question);
            return "questions/edit";
        } catch (ChangeSetPersister.NotFoundException e) {
            return "error/404"; // Create an HTML template for the 404 error page
        }
    }

    @PostMapping("/edit/{id}")
    public String updateQuestion(@PathVariable Integer id, @ModelAttribute("question") Question updatedQuestion) {
        try {
            questionService.updateQuestion(id, updatedQuestion);
            return "redirect:/questions";
        } catch (ChangeSetPersister.NotFoundException e) {
            return "error/404"; // Create an HTML template for the 404 error page
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Integer id) {
        questionService.deleteQuestion(id);
        return "redirect:/questions";
    }

    @GetMapping("/toggle/{id}")
    public String toggleQuestionStatus(@PathVariable Integer id) {
        questionService.toggleQuestionStatus(id);
        return "redirect:/questions";
    }
}
