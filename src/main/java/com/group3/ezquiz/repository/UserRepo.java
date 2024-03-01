package com.group3.ezquiz.repository;

import com.group3.ezquiz.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
  User findByEmail(String email);

  //@query for searching by name and email and filtering by status
  @Query("SELECT u FROM User u WHERE " +
          "((:email IS NULL OR u.email LIKE %:email%) OR " +
          "(:name IS NULL OR u.fullName LIKE %:name%)) " +
          " AND (:status IS NULL OR u.isEnable = :status)")
  Page<User> getAllUser(String email,String name, Boolean status, Pageable page);

  User findUserById(Long id);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
  boolean existsByEmail(@Param(value = "email") String email);

}