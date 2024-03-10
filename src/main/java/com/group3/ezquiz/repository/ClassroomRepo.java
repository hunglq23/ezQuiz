package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.User;

import java.util.List;

@Repository
public interface ClassroomRepo extends JpaRepository<Classroom, Long> {
        // @Query ("Select c from Classroom c where" +
        // "(:sName is null or c.className Like %:sName%)")
        // Page<Classroom> getAllClassroom(String sName, Pageable page);
        Classroom findClassById(Long id);

        List<Classroom> findByCreator(User creator);

        Classroom findByIdAndCreator(Long id, User creator);

        Classroom findByCode(String code);

}
