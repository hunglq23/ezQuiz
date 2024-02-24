package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.QuizDto;
import com.group3.ezquiz.repository.QuizRepo;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.IQuizService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements IQuizService {

    private final static Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);
    private final QuizRepo quizRepository;
    private final UserRepo userRepo;

    @Override
    public Page<Quiz> listAll(HttpServletRequest http, String searchTerm, Pageable pageable) {
        return quizRepository.getAllQuiz(searchTerm, searchTerm, pageable);
    }

    @Override
    public void toggleQuizStatus(Integer id) {
        Quiz existedQuiz = quizRepository.findQuizById(id);

        if (existedQuiz != null) {
            existedQuiz.setIsActive(!existedQuiz.getIsActive());
            quizRepository.save(existedQuiz);
        }
    }

    @Override
    public boolean existedQuizByCode(String code) {
        return quizRepository.existsQuizByCode(code);
    }

    @Override
    public void createQuiz(HttpServletRequest request, QuizDto quizDto) {
        if (existedQuizByCode(quizDto.getCode())) {
            throw new IllegalArgumentException("A quiz with the same code already existed");
        }
        quizRepository.save(
                Quiz.builder()
                        .code(quizDto.getCode())
                        .title(quizDto.getTitle())
                        .description(quizDto.getDescription())
                        .isExamOnly(quizDto.getIsExamOnly())
                        .isActive(quizDto.getIsActive())
                        .createdBy(getUserRequesting(request))
                        .build());
    }

    @Override
    public Quiz getQuizById(String stringId) {
        Integer id = getIntegerId(stringId);
        if (id != null) {
            Optional<Quiz> byId = quizRepository.findById(id);
            if (byId.isPresent())
                return byId.get();
        }
        throw new ResourceNotFoundException("Not found quiz with ID: " + stringId);
    }

    @Override
    public Quiz findQuizById(Integer id) {
        Quiz quizById = quizRepository.findQuizById(id);
        if (quizById != null) {
            return quizById;
        }
        throw new ResourceNotFoundException("Cannot find quiz with" + id);
    }

    @Override
    public void deleteQuiz(Integer id) {
        Optional<Quiz> optionalQuiz = quizRepository.findById(id);
        if (optionalQuiz.isPresent()) {
            Quiz existedQuiz = optionalQuiz.get();
            quizRepository.delete(existedQuiz);
        } else {
            throw new ResourceNotFoundException("Quiz with id " + id + "not found!");
        }
    }

    @Override
    public void updateQuiz(HttpServletRequest request, Integer id, QuizDto updateQuiz) {

        User userRequesting = getUserRequesting(request);

        Quiz existedQuiz = findQuizById(id);
        // if(existedQuizByCode(updateQuiz.getCode())){
        // throw new IllegalArgumentException("A quiz with the same code already
        // existed");
        // }
        Quiz saveQuiz = Quiz.builder()
                // unchangeable
                .id(existedQuiz.getId())
                .createdAt(existedQuiz.getCreatedAt())
                .createdBy(existedQuiz.getCreatedBy())
                // to update
                .code(updateQuiz.getCode())
                .title(updateQuiz.getTitle())
                .description(updateQuiz.getDescription())
                .isActive(updateQuiz.getIsActive())
                .isExamOnly(updateQuiz.getIsExamOnly())
                .build();
        quizRepository.save(saveQuiz);
    }

    private User getUserRequesting(HttpServletRequest http) {
        Principal userPrincipal = http.getUserPrincipal();
        String requestingUserEmail = userPrincipal.getName();
        User requestingUser = userRepo.findByEmail(requestingUserEmail);
        return requestingUser;
    }

    private Integer getIntegerId(String stringId) {

        try {
            return Integer.parseInt(stringId);

        } catch (NumberFormatException e) {
            log.error("Cannot parse ID " + stringId + " to Integer");
        }
        return null;
    }

}
