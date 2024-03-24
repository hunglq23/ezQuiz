//package com.group3.ezquiz.repository;
//
//import java.util.List;
//import java.util.UUID;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.group3.ezquiz.model.QuizUUID;
//import com.group3.ezquiz.model.User;
//import org.springframework.data.jpa.repository.Query;
//
//public interface QuizUUIDRepo extends JpaRepository<QuizUUID, UUID> {
//
//  QuizUUID findByIdAndCreator(UUID id, User creator);
//
//  @Query(value = "select q from QuizUUID q order by q.updatedAt DESC limit 3")
//  List<QuizUUID> findQuizUUID();
//}
