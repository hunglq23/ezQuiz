package com.group3.ezquiz.controller.rest;

import com.group3.ezquiz.payload.quiz.QuizDetail;
import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.service.IQuizService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public List<QuizDto> searchLib(
            HttpServletRequest request,
            @RequestParam(value = "search") String search,
            Model model) {
        return quizService.searchQuizUUID(request, search);
    }

    @PreAuthorize(TEACHER_AUTHORITY)
    @GetMapping("/{id}")
    public QuizDetail getQuizSearch(
            HttpServletRequest request,
            @PathVariable UUID id) {

        return quizService.getQuizWhenSearch(id);
    }
}
