package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Quest;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.model.QuizUUID;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.QuizDetailsDto;
import com.group3.ezquiz.payload.QuizDto;
import com.group3.ezquiz.repository.IQuestRepo;
import com.group3.ezquiz.repository.QuizRepo;
import com.group3.ezquiz.repository.QuizUUIDRepo;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.IQuestService;
import com.group3.ezquiz.service.IQuizService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements IQuizService {

    private final static Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);
    private final QuizUUIDRepo quizUUIDRepo;
    private final IQuestRepo questionRepo;
    private final IQuestService questionService;

    private final UserRepo userRepo;

    private final QuizRepo qRepo;

    @Override
    public QuizUUID saveAndGetDraftQuiz(HttpServletRequest http) {
        User userRequesting = getUserRequesting(http);
        QuizUUID quizUUID = QuizUUID.builder()
                .isDraft(true)
                .isEnable(true)
                .isExam(false)
                .creator(userRequesting)
                .build();
        QuizUUID saved = quizUUIDRepo.save(quizUUID);
        return saved;

    }

    @Override
    public QuizUUID getQuizByRequestAndUUID(HttpServletRequest request, UUID id) {
        User userRequesting = getUserRequesting(request);
        QuizUUID byIdAndCreator = quizUUIDRepo.findByIdAndCreator(id, userRequesting);

        if (byIdAndCreator != null) {
            return byIdAndCreator;
        }
        throw new ResourceNotFoundException("Not found your quiz with ID " + id);
    }

    @Override
    public ResponseEntity<?> handleQuizUpdatingRequest(
            HttpServletRequest request,
            UUID id,
            QuizDetailsDto dto) {
        User userRequesting = getUserRequesting(request);
        QuizUUID quiz = quizUUIDRepo.findByIdAndCreator(id, userRequesting);
        Set<Quest> questions = quiz.getQuestions();
        questions.add(questionRepo.findById(3L).get());
        questions.add(questionRepo.findById(4L).get());
        quizUUIDRepo.save(quiz);

        return null;
    }

    @Override
    public String handleQuestionCreatingInQuiz(
            HttpServletRequest request,
            QuizUUID quiz,
            String type,
            String questionText,
            Map<String, String> params) {
        Quest newQuestion = questionService.createNewQuestion(request, type, questionText, params);
        quiz.getQuestions().add(newQuestion);
        quizUUIDRepo.save(quiz);
        return "redirect:/quiz/" + quiz.getId() + "/edit#" + newQuestion.getId();
    }

    @Override
    public Page<Quiz> listAll(HttpServletRequest http, String searchTerm, Pageable pageable) {
        return qRepo.getAllQuiz(searchTerm, searchTerm, pageable);
    }

    @Override
    public void toggleQuizStatus(Integer id) {
        Quiz existedQuiz = qRepo.findQuizById(id);

        if (existedQuiz != null) {
            existedQuiz.setIsActive(!existedQuiz.getIsActive());
            qRepo.save(existedQuiz);
        }
    }

    @Override
    public boolean existedQuizByCode(String code) {
        return qRepo.existsQuizByCode(code);
    }

    @Override
    public void createQuiz(HttpServletRequest request, QuizDto quizDto) {
        if (existedQuizByCode(quizDto.getCode())) {
            throw new IllegalArgumentException("A quiz with the same code already existed");
        }
        qRepo.save(
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
            Optional<Quiz> byId = qRepo.findById(id);
            if (byId.isPresent())
                return byId.get();
        }
        throw new ResourceNotFoundException("Not found quiz with ID: " + stringId);
    }

    @Override
    public Quiz findQuizById(Integer id) {
        Quiz quizById = qRepo.findQuizById(id);
        if (quizById != null) {
            return quizById;
        }
        throw new ResourceNotFoundException("Cannot find quiz with" + id);
    }

    @Override
    public void deleteQuiz(Integer id) {
        Optional<Quiz> optionalQuiz = qRepo.findById(id);
        if (optionalQuiz.isPresent()) {
            Quiz existedQuiz = optionalQuiz.get();
            qRepo.delete(existedQuiz);
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
        qRepo.save(saveQuiz);
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
