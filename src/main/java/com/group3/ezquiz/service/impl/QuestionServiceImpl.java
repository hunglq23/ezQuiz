package com.group3.ezquiz.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.payload.QuestionDto;
import com.group3.ezquiz.repository.OptionRepo;
import com.group3.ezquiz.repository.QuestionRepo;
import com.group3.ezquiz.service.IQuestionService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private OptionRepo optionRepo;

    @Autowired
    private HttpServletResponse response;

    @Override
    public void createNewQuestion(QuestionDto dto, Map<String, String> params) {
        // Create a new question
        Question question = Question.builder()
                .questionCode(dto.getQuestionCode())
                .text(dto.getText())
                .isActive(true) // Assuming new questions are active by default
                .build();
        // Save the question to the database using questionRepo
        questionRepo.save(question);
        List<Option> options = new ArrayList<>();
        Option currentOption = null;
        // Iterate through the params to create options
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String option = entry.getKey();
            String optionText = entry.getValue();
            // Check if the key starts with "option"
            if (option.startsWith("option")) {
                // Create a new option
                currentOption = Option.builder()
                        .question(question) // Associate the option with the question
                        .text(optionText)
                        .isAnswer(false) // Default to false
                        .build();
                // Add the option to the list
                options.add(currentOption);
            } else if (option.startsWith("answer")) {
                // Update the current option's isAnswer if it exists
                if (currentOption != null) {
                    currentOption.setAnswer(true);
                }
                currentOption = null;
            }
        }
        // Save the options to the database using optionRepo
        optionRepo.saveAll(options);

        // Check if at least one correct answer is set
        if (options.stream().noneMatch(Option::isAnswer)) {
            // No correct answer, perform a redirect
            try {
                response.sendRedirect("/questions");
            } catch (IOException e) {
                // Handle the IOException, if needed
                e.printStackTrace();
            }
        }
    }

    public List<Question> getAllQuestions() {
        return questionRepo.findAll();
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepo.findById(id);
    }

    public Question updateQuestion(Long id, Question question) {
        // Step 1: Check if the question with the given ID exists
        Optional<Question> optionalQuestion = questionRepo.findById(id);
        if (optionalQuestion.isPresent()) {
            Question existingQuestion = optionalQuestion.get();

            existingQuestion.setText(question.getText());

            if (question.getQuestionCode() != null) {
                existingQuestion.setQuestionCode(question.getQuestionCode());
            }

            Question updatedQuestion = questionRepo.save(existingQuestion);

            // Step 7: Log the update for debugging
            System.out.println("Question with ID " + id + " updated: " + updatedQuestion);

            // Step 8: Return the updated question
            return updatedQuestion;
        } else {
            // Step 9: Throw an exception if the question with the given ID is not found
            throw new ResourceNotFoundException("Question with ID " + id + " not found");
        }
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

    @Override
    public List<Question> getQuestionsForUser(Long numOfQuestion) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getQuestionsForUser'");
    }

    public void toggleQuestionStatus(Long questionId) {
        Optional<Question> questionOptional = questionRepo.findById(questionId);

        if (questionOptional.isPresent()) {
            Question question = questionOptional.get();
            // Toggle the question status
            question.setActive(!question.isActive());
            questionRepo.save(question);
        } else {
            // Handle the case where the question does not exist
            // You might throw an exception or log a message
        }
    }

}
