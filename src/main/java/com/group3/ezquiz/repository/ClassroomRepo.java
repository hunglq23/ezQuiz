package com.group3.ezquiz.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Classroom;

@Repository
public interface ClassroomRepo extends JpaRepository<Classroom, Long>{
    @Query("Select c from Classroom c where c.className like  %?1%")
    public List<Classroom> findAll(String keyword);
}
