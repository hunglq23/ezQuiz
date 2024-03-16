package com.group3.ezquiz.controller.rest;

import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.service.IQuizService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('TEACHER')")
@RequiredArgsConstructor
@RequestMapping("/rest-quiz")
public class QuizRestController {
    // private final String LEARNER_AUTHORITY = "hasRole('ROLE_LEARNER')";
    private final String TEACHER_AUTHORITY = "hasRole('ROLE_TEACHER')";

    private final IQuizService quizService;

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/search")
    public List<Quiz> searchLib(
            HttpServletRequest request,
            @RequestParam(value = "search") String search,
            Model model) {
        return quizService.searchQuizUUID(request, search);
    }
}
