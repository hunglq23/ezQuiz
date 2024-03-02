// package com.group3.ezquiz.repository;

// import com.group3.ezquiz.model.Quiz;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;

// @Repository
// public interface QuizRepo extends JpaRepository<Quiz, Integer> {
// Quiz findQuizByQuizId(Integer id);

// boolean existsQuizByCode(String code);

// @Query("SELECT q FROM Quiz q WHERE " +
// "(:code IS NULL OR q.code LIKE %:code%) OR " +
// "(:title IS NULL OR q.title LIKE %:title%)")
// Page<Quiz> getAllQuiz(String code, String title, Pageable page);
// }
