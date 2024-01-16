package com.group3.ezquiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.repository.QuestionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService implements IQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Optional<Question> getQuestionById(Integer id) {
        return questionRepository.findById(id);
    }

    @Override
    public Question updateQuestion(Integer id, Question updatedQuestion) throws NotFoundException {
        Optional<Question> existingQuestionOptional = questionRepository.findById(id);
        if (existingQuestionOptional.isPresent()) {
            Question existingQuestion = existingQuestionOptional.get();
            // Update fields of existingQuestion with values from updatedQuestion
            existingQuestion.setContent(updatedQuestion.getContent());
            existingQuestion.setActive(updatedQuestion.isActive());
            existingQuestion.setOptions(updatedQuestion.getOptions());
            // Save the updated question
            return questionRepository.save(existingQuestion);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public void deleteQuestion(Integer id) {
        questionRepository.deleteById(id);
    }

    @Override
    public List<Question> getQuestionsForUser(Integer numOfQuestions) {
        // Implement logic to fetch a specific number of questions
        // For example, you might use questionRepository.findTopN(numOfQuestions)
        // Ensure that the implementation meets your specific requirements
        return null;
    }
}
