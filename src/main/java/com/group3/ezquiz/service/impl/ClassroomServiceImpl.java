package com.group3.ezquiz.service.impl;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.payload.ClassroomDto;
import com.group3.ezquiz.repository.ClassroomRepo;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.ClassroomService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    @Autowired
    private ClassroomRepo classroomRepo;
    @Autowired
    private UserRepo userRepo;

 
   

    @Override
    public void deleteClassroomById(Long id) {
        Optional<Classroom> optional = classroomRepo.findById(id);
        if (optional.isPresent()) {
            Classroom classroom = optional.get();
            classroomRepo.delete(classroom);
        } else {
            throw new EntityNotFoundException("Classroom with id " + id + " not found");
        }
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

    @Override
    public void createClass(HttpServletRequest request, ClassroomDto dto) {
         Principal principal = request.getUserPrincipal(); // chua thong tin user hien tai

            // Tiếp tục xử lý khi có người dùng xác thực
            classroomRepo.save(
                    Classroom.builder()
                            .className(dto.getClassName())
                            .description(dto.getDescription())
                            .creator(userRepo.findByEmail(principal.getName()))
                            .build());
        }

    @Override
    public Page<Classroom> getAllClass(int pageNumber,  String keyword) {
       Pageable pageable = PageRequest.of(pageNumber-1, 6);
    if(keyword != null){
        return classroomRepo.findAll(keyword,pageable);
    }
    return classroomRepo.findAll(pageable);
    }
    }
