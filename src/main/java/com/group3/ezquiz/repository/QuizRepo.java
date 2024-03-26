package com.group3.ezquiz.repository;

import com.group3.ezquiz.model.Quiz;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.group3.ezquiz.model.User;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, UUID> {

        Optional<Quiz> findByIdAndCreator(UUID id, User creator);

        @Query("SELECT q FROM Quiz q WHERE q.creator = :creator AND ( q.isDraft = :isDraft) " +
                        "ORDER BY CASE WHEN :sort = 'latest' THEN q.createdAt END DESC, " +
                        "CASE WHEN :sort = 'oldest' THEN q.createdAt END ASC")
        Page<Quiz> findByCreatorAndIsDraftAndSort(@Param("creator") User creator,
                        @Param("isDraft") Boolean isDraft,
                        @Param("sort") String sortOrder,
                        Pageable pageable);

        @Query("SELECT q FROM Quiz q WHERE q.creator = :creator " +
                        "ORDER BY CASE WHEN :sort = 'latest' THEN q.createdAt END DESC, " +
                        "CASE WHEN :sort = 'oldest' THEN q.createdAt END ASC")
        Page<Quiz> findByCreatorAndSort(@Param("creator") User creator,
                        @Param("sort") String sortOrder,
                        Pageable pageable);

        List<Quiz> findByCreator(User userRequesting);

        @Query(value = "select q from Quiz q where q.title like %:search%")
        List<Quiz> searchQuizUUID(@Param(value = "search") String search);

        Page<Quiz> findByIsDraftIsFalseAndTitleContaining(String title, Pageable pageable);

        Page<Quiz> findByCreatorAndIsDraftAndTitleContaining(
                        User creator, Boolean isDraft, String title, Pageable pageable);

        Page<Quiz> findByCreatorAndTitleContaining(User creator, String title, Pageable pageable);

        List<Quiz> findTop4ByIsEnableIsTrueAndIsDraftIsFalseOrderByCreatedAtDesc();

        List<Quiz> findTop4ByIsEnableIsTrueAndIsDraftIsFalseOrderByCreatedAt();

}
