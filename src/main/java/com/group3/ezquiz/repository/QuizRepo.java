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

  // @Query("SELECT TOP 2 FROM Quiz q WHERE q.creator = :creator " +
  // "AND q.title LIKE %:search%" +
  // "ORDER BY CASE WHEN :sort = 'latest' THEN q.createdAt END DESC, " +
  // "CASE WHEN :sort = 'oldest' THEN q.createdAt END ASC")
  // List<Quiz> findByCreatorAndSearchAndSortAndNum(User creator, String search,
  // String sort, Integer itemNum);

  // findByCreatorAndCreatedAtAsc();
  // Page<Quiz> findByCreator(User creator, PageRequest.of(0, 1,
  // Sort.by(Sort.Direction.ASC, "seatNumber"));
  Page<Quiz> findByCreatorAndTitleContaining(User creator, String title, Pageable pageable);

  List<Quiz> findByCreator(User userRequesting);

  Quiz findQuizById(UUID id);

  // @Query("SELECT q FROM Quiz q WHERE " +
  // "(:code IS NULL OR q.code LIKE %:code%) OR " +
  // "(:title IS NULL OR q.title LIKE %:title%)")
  // Page<Quiz> getAllQuiz(String code, String title, Pageable page);

  @Query(value = "select q from Quiz q order by q.updatedAt DESC limit 3")
  List<Quiz> findQuizUUID();

  @Query(value = "select q from Quiz q where q.title like %:search%")
  List<Quiz> searchQuizUUID(@Param(value = "search") String search);
}
