package com.group3.ezquiz.service.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.QuestionDto;
import com.group3.ezquiz.repository.OptionRepo;
import com.group3.ezquiz.repository.QuestionRepo;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.IQuestionService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private OptionRepo optionRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void createNewQuestion(HttpServletRequest request, QuestionDto dto, Map<String, String> params) {
        Principal principal = request.getUserPrincipal(); // chua thong tin user hien tai

        // Create a new question
        Question question = Question.builder()
                .questionCode(dto.getQuestionCode())
                .text(dto.getText())
                .isActive(true) // Assuming new questions are active by default
                .createdBy(userRepo.findByEmail(principal.getName()))
                .build();
        List<Option> options = new ArrayList<>();
        boolean atLeastOneCorrectAnswer = false;
        // Iterate through the params to create options
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String optionKey = entry.getKey();
            String optionText = entry.getValue();
            // Check if the key starts with "option"
            if (optionKey.startsWith("option")) {
                boolean isAnswer = params.containsKey("answer" + optionKey.substring("option".length()));
                Option option = Option.builder()
                        .question(question)
                        .text(optionText)
                        .isAnswer(isAnswer)
                        .build();
                // Add the option to the list
                options.add(option);
                // Check if the current option is a correct answer
                if (isAnswer) {
                    atLeastOneCorrectAnswer = true;
                }
            }
        }
        // Check if at least one correct answer is set
        if (!atLeastOneCorrectAnswer) {
            throw new IllegalArgumentException("At least one correct answer must be set.");
        }
        questionRepo.save(question);
        optionRepo.saveAll(options);
    }

    @Override
    public Page<Question> listAll(HttpServletRequest http, String searchTerm, Pageable pageable) {
        return questionRepo.getAllQuestions(searchTerm, searchTerm, pageable);
    }

    public void updateQuestion(Long id, Question question) {
        // Step 1: Check if the question with the given ID exists
        Question existQuestion = questionRepo.findQuestionByQuestionId(id);
        Question saveQuestion = Question.builder()
                // unchangeable
                .questionId(existQuestion.getQuestionId())
                .options(existQuestion.getOptions())
                // to update
                .questionCode(question.getQuestionCode())
                .text(question.getText())
                .build();
        questionRepo.save(saveQuestion);
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @Override
    public void deleteQuestion(Long questionId) {
        Optional<Question> optionalQuestion = questionRepo.findById(questionId);
        System.out.println("abcsac");
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            questionRepo.delete(question);
        } else {
            throw new EntityNotFoundException("Question with id " + questionId + " not found");
        }
    }

    public void toggleQuestionStatus(Long questionId) {
        Optional<Question> questionOptional = questionRepo.findById(questionId);

        if (questionOptional.isPresent()) {
            Question question = questionOptional.get();
            // Toggle the question status
            question.setActive(!question.isActive());
            questionRepo.save(question);
        }
    }

}
