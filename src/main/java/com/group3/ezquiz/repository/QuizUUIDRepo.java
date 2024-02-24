package com.group3.ezquiz.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.ezquiz.model.QuizUUID;
import com.group3.ezquiz.model.User;

public interface QuizUUIDRepo extends JpaRepository<QuizUUID, UUID> {

  QuizUUID findByIdAndCreator(UUID id, User creator);
}
