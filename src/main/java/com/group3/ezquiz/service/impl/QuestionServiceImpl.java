package com.group3.ezquiz.service.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        Principal principal = request.getUserPrincipal();

        String questionText = dto.getText();
        if (questionRepo.existsByText(questionText)) {
            throw new IllegalArgumentException("A question with the same text already exists.");
        }

        Question question = Question.builder()
                .text(dto.getText())
                .isActive(true) // Assuming new questions are active by default
                .createdBy(userRepo.findByEmail(principal.getName()))
                .build();
        List<Option> options = new ArrayList<>();
        Set<String> optionTexts = new HashSet<>();
        boolean atLeastOneCorrectAnswer = false;
        // Iterate through the params to create options
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String optionKey = entry.getKey();
            String optionText = entry.getValue();
            // Check if the key starts with "option"
            if (optionKey.startsWith("option")) {
                if (!optionTexts.add(optionText)) {
                    throw new IllegalArgumentException("Duplicate option text found: " + optionText);
                }
                boolean isAnswer = params.containsKey("answer" + optionKey.substring("option".length()));
                Option option = Option.builder()
                        .question(question)
                        .text(optionText)
                        .isAnswer(isAnswer)
                        .build();
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
        return questionRepo.getAllQuestions(searchTerm, pageable);
    }

    public void updateQuestion(Long id, Question question) {
        // Check if the question with the given ID exists
        Question existQuestion = questionRepo.findQuestionByQuestionId(id);
        Question saveQuestion = Question.builder()
                // unchangeable
                .questionId(existQuestion.getQuestionId())
                .options(existQuestion.getOptions())
                // to update
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
            question.setActive(!question.isActive());
            questionRepo.save(question);
        }
    }

}
