// package com.group3.ezquiz.service.impl;

// import com.group3.ezquiz.model.Question;
// import com.group3.ezquiz.model.Quiz;
// import com.group3.ezquiz.model.User;
// import com.group3.ezquiz.payload.QuizDto;
// import com.group3.ezquiz.repository.QuizRepo;
// import com.group3.ezquiz.repository.UserRepo;
// import com.group3.ezquiz.service.IQuizService;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;

// import java.security.Principal;
// import java.util.Optional;

// @Service
// @RequiredArgsConstructor
// public class QuizServiceImpl implements IQuizService {

// private final QuizRepo quizRepository;
// private final UserRepo userRepo;

// @Override
// public Page<Quiz> listAll(HttpServletRequest http, String searchTerm,
// Pageable pageable) {
// return quizRepository.getAllQuiz(searchTerm, searchTerm, pageable);
// }

// @Override
// public void toggleQuizStatus(Integer id) {
// Quiz existedQuiz = quizRepository.findQuizByQuizId(id);

// if (existedQuiz != null) {
// existedQuiz.setIsActive(!existedQuiz.getIsActive());
// quizRepository.save(existedQuiz);
// }
// }

// @Override
// public boolean existedQuizByCode(String code) {
// return quizRepository.existsQuizByCode(code);
// }

// @Override
// public void createQuiz(HttpServletRequest request, QuizDto quizDto) {
// if(existedQuizByCode(quizDto.getCode())){
// throw new IllegalArgumentException("A quiz with the same code already
// existed");
// }
// quizRepository.save(
// Quiz.builder()
// .code(quizDto.getCode())
// .title(quizDto.getTitle())
// .description(quizDto.getDescription())
// .isExamOnly(quizDto.getIsExamOnly())
// .isActive(quizDto.getIsActive())
// .createdBy(getUserRequesting(request))
// .build());
// }

// @Override
// public Quiz findQuizById(Integer id) {
// //
// Quiz quizById = quizRepository.findQuizByQuizId(id);
// if (quizById != null) {
// if (quizById.getUpdatedBy() == null) {
// quizById.setUpdateAt(null);
// }
// return quizById;
// }
// throw new ResourceNotFoundException("Cannot find quiz with" + id);
// }

// @Override
// public void deleteQuiz(Integer id) {
// Optional<Quiz> optionalQuiz = quizRepository.findById(id);
// if (optionalQuiz.isPresent()) {
// Quiz existedQuiz = optionalQuiz.get();
// quizRepository.delete(existedQuiz);
// } else {
// throw new ResourceNotFoundException("Quiz with id " + id + "not found!");
// }
// }

// @Override
// public void updateQuiz(HttpServletRequest request, Integer id, QuizDto
// updateQuiz) {

// User userRequesting = getUserRequesting(request);

// Quiz existedQuiz = findQuizById(id);
// // if(existedQuizByCode(updateQuiz.getCode())){
// // throw new IllegalArgumentException("A quiz with the same code already
// existed");
// // }
// Quiz saveQuiz = Quiz.builder()
// // unchangeable
// .quizId(existedQuiz.getQuizId())
// .createdAt(existedQuiz.getCreatedAt())
// .createdBy(existedQuiz.getCreatedBy())
// // to update
// .code(updateQuiz.getCode())
// .title(updateQuiz.getTitle())
// .description(updateQuiz.getDescription())
// .isActive(updateQuiz.getIsActive())
// .isExamOnly(updateQuiz.getIsExamOnly())
// .updatedBy(userRequesting.getId())
// .build();
// quizRepository.save(saveQuiz);
// }

// public class ResourceNotFoundException extends RuntimeException {
// public ResourceNotFoundException(String message) {
// super(message);
// }
// }

// // public class DuplicateException extends RuntimeException {
// // public DuplicateException(String message) {
// // super(message);
// // }
// // }

// private User getUserRequesting(HttpServletRequest http) {
// Principal userPrincipal = http.getUserPrincipal();
// String requestingUserEmail = userPrincipal.getName();
// User requestingUser = userRepo.findByEmail(requestingUserEmail);
// return requestingUser;
// }
// }
