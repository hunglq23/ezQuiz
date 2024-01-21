package com.group3.ezquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.ezquiz.model.Classroom;

@Repository
public interface ClassroomRepo extends JpaRepository<Classroom, Long>{

}
