package com.group3.ezquiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.group3.ezquiz.model.Question;
import com.group3.ezquiz.repository.QuestionRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;
    private final EntityManager entityManager;

    public QuestionService(QuestionRepository questionRepository, EntityManager entityManager) {
        this.questionRepository = questionRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void createQuestion(Question question) {
        // Your code to save the question
        questionRepository.save(question);

        // Flush the EntityManager
        entityManager.flush();
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

    @Override
    public void toggleQuestionStatus(Integer questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        optionalQuestion.ifPresent(question -> {
            question.setActive(!question.isActive());
            questionRepository.save(question);
        });
    }
}
