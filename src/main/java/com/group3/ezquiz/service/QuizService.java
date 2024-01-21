package com.group3.ezquiz.service;

import com.group3.ezquiz.model.Quiz;

import com.group3.ezquiz.payload.QuizDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;


public interface QuizService {

    List<Quiz> listAll();
    void createQuiz(HttpServletRequest request, QuizDto quizDto);
    Optional<Quiz> findQuizById(Integer id);
    Quiz updateQuiz(Integer id, Quiz quiz) throws ChangeSetPersister.NotFoundException;
    void deleteQuiz(Integer id);

    Page<Quiz> paginated(Integer pageNo, Integer pageSize);


}
