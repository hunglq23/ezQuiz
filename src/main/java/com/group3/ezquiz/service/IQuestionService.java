// package com.group3.ezquiz.service;

// import java.util.List;
// import java.util.Map;

// import
// org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

// import com.group3.ezquiz.model.Question;
// import com.group3.ezquiz.payload.QuestionDto;

// import jakarta.servlet.http.HttpServletRequest;

// public interface IQuestionService {

// void createNewQuestion(HttpServletRequest request, QuestionDto dto,
// Map<String, String> params);

// Page<Question> listAll(HttpServletRequest http, String searchTerm, Pageable
// pageable);

// void updateQuestion(Long id, Question question) throws NotFoundException;

// void deleteQuestion(Long id);

// public void toggleQuestionStatus(Long questionId);

// }
