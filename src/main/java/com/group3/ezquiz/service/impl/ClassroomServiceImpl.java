package com.group3.ezquiz.service.impl;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.User;
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
        String code = generateClassCode(); // Fix cung code "CODE123456"

        // Tiếp tục xử lý khi có người dùng xác thực
        classroomRepo.save(
                Classroom.builder()
                        .className(dto.getClassName())
                        .description(dto.getDescription())
                        .code(code)
                        .isEnable(true)
                        .creator(getUserRequesting(request))
                        .build());
    }

    @Override
    public Page<Classroom> getClassListByPageAndSearchName(Integer page, String searchName) {

        return classroomRepo.getAllClassroom(searchName, PageRequest.of(page, 5));
    }

    private User getUserRequesting(HttpServletRequest http) {
        Principal userPrincipal = http.getUserPrincipal();
        String requestingUserByEmail = userPrincipal.getName();
        User requestingUser = userRepo.findByEmail(requestingUserByEmail);
        return requestingUser;
    }

    private String generateClassCode() {
        UUID codeUUID = UUID.randomUUID();
        String codeClass = codeUUID.toString().replace("-", "").substring(0, 8).toUpperCase();
        return codeClass;
    }

    @Override
    public Classroom findByCode(String code) {
        return classroomRepo.findByCode(code);
    }

    @Override
    public void saveClass(Classroom classroom) {
        classroomRepo.save(classroom);
    }

    @Override
    public boolean joinClassroom(HttpServletRequest request, String code) {
        User learner = getUserRequesting(request);
        Classroom classroom = classroomRepo.findByCode(code);
        if (classroom != null && learner != null) {
            classroom.getMembers().add(learner);
            classroomRepo.save(classroom);
            return true;
        }
        return false;
    }

    @Override
    public List<Classroom> getAllClassroom() {
        return classroomRepo.findAll();
    }

}
