package com.group3.ezquiz.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Classroom;

import com.group3.ezquiz.repository.ClassroomRepo;

import com.group3.ezquiz.service.ClassroomService;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    @Autowired
    private ClassroomRepo classroomRepo;
    // @Autowired
    // private UserRepo userRepo;

    @Override
    public List<Classroom> getAllClass() {
        return classroomRepo.findAll();
    }

    @Override
    public void createClass(Classroom classroom) {
        classroomRepo.save(classroom);
    }
}