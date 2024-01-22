package com.group3.ezquiz.service.impl;

import java.util.List;
import java.util.Optional;

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
    public List<Classroom> getAllClass(String keyword) {
        if(keyword != null){
            return classroomRepo.findAll(keyword);
        }
        return classroomRepo.findAll();
    }

    @Override
    public void createClass(Classroom classroom) {
        classroomRepo.save(classroom);
    }

  

    @Override
    public void deleteClassroomById(Long id) {
        classroomRepo.deleteById(id);
    }

    @Override
    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepo.findById(id);
    }
    
    @Override
    public Classroom updateClassroom(Long id, Classroom updatedClassroom) {
        Optional<Classroom> optional = classroomRepo.findById(id);
    
        if (optional.isPresent()) {
            Classroom classroomToUpdate = optional.get();
            // Cập nhật thông tin lớp học với dữ liệu mới
            classroomToUpdate.setClassName(updatedClassroom.getClassName());
            classroomToUpdate.setDescription(updatedClassroom.getDescription());
    
            // Lưu lớp học đã cập nhật vào cơ sở dữ liệu và trả về kết quả
             classroomRepo.save(classroomToUpdate);
             System.out.println("Classroom updated sucessfully");
             return classroomToUpdate;
        } else {
            throw new RuntimeException("Classroom not found for id: " + id);
        }
    }
}