package com.group3.ezquiz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Question;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Long> {

    Question findByText(String text);

    Question findQuestionByQuestionId(Long questionId);

    Page<Question> findByTextContainingIgnoreCase(String searchText, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE " +
            "(:questionCode IS NULL OR q.questionCode LIKE %:questionCode%) OR " +
            "(:text IS NULL OR q.text LIKE %:text%)")
    Page<Question> getAllQuestions(@Param("questionCode") String questionCode,
            @Param("text") String text,
            Pageable page);

}
