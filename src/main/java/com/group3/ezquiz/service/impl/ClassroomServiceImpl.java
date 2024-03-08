package com.group3.ezquiz.service.impl;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.ClassroomDto;

import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.repository.ClassroomRepo;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.IClassroomService;
import com.group3.ezquiz.service.IUserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements IClassroomService {

    // log loi
    private final static Logger log = LoggerFactory.getLogger(ClassroomServiceImpl.class);

    private final ClassroomRepo classroomRepo;
    private final IUserService userService;
    private final UserRepo userRepo;

    @Override
    public List<Classroom> getCreatedClassrooms(HttpServletRequest request) {
        User creator = userService.getUserRequesting(request);
        return classroomRepo.findByCreator(creator);
    }

    @Override
    public ResponseEntity<?> createClass(HttpServletRequest request, ClassroomDto dto) {
        String code = generateClassCode();
        // Tiếp tục xử lý khi có người dùng xác thực
        Classroom classroom = Classroom.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .code(code)
                .creator(userService.getUserRequesting(request))
                .isEnable(true)
                .build();
        if (classroom != null) {
            classroomRepo.save(classroom);
            return new ResponseEntity<>(
                    MessageResponse.builder()
                            .message("Created Class Successfully!")
                            .timestamp(LocalDateTime.now())
                            .build(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                MessageResponse.builder()
                        .message("Created Class Fail!")
                        .timestamp(LocalDateTime.now())
                        .build(),
                HttpStatus.BAD_REQUEST);
    };

    private String generateClassCode() {
        UUID codeUUID = UUID.randomUUID();
        String codeClass = codeUUID.toString().replace("-", "").substring(0, 8).toUpperCase();
        return codeClass;
    }

    @Override
    public Classroom getClassroomByRequestAndId(HttpServletRequest request, Long id) {
        User creator = userService.getUserRequesting(request);
        return classroomRepo.findByIdAndCreator(id, creator);
    }

    private Classroom findClassById(Long id) {
        Classroom classById = classroomRepo.findClassById(id);
        if (classById != null) {
            return classById;
        }
        throw new ResourceNotFoundException("Can not find classroom");
    }

    @Override
    public Classroom updateClassroom(Long id, Classroom updatedClassroom) {
        Optional<Classroom> optional = classroomRepo.findById(id);

        if (optional.isPresent()) {
            Classroom classroomToUpdate = optional.get();
            // Cập nhật thông tin lớp học với dữ liệu mới
            classroomToUpdate.setName(updatedClassroom.getName());
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
    public void deleteClassById(Long id) {
        Classroom existClassroom = findClassById(id);
        if (existClassroom != null) {
            classroomRepo.delete(existClassroom);
        }
    }

    @Override
    public Classroom findByCode(String code) {
        
        return classroomRepo.findByCode(code);
    }

    @Override
    public boolean joinClassroom(HttpServletRequest request, String code) {
        User learner = userService.getUserRequesting(request);
        Classroom classroom = classroomRepo.findByCode(code);
        if(classroom != null && learner != null){
            classroom.getMembers().add(learner);
            classroomRepo.save(classroom);
            learner.getClassrooms().add(classroom);
            userRepo.save(learner);
            return true;
        }
        return false;
    }

    @Override
    public Classroom removeMemberFromClassroomByMemberId(Classroom classroom, Long memberId) {
        User member = userRepo.findUserById(memberId);
        member.getClassrooms().remove(classroom);
        userRepo.save(member);
        classroom.getMembers().remove(member);
       return classroomRepo.save(classroom);
        
    }

    



}
