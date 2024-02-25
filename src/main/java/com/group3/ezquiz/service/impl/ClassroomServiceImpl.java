package com.group3.ezquiz.service.impl;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
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
    public Optional<Classroom> getClassroomByRequestAndId(HttpServletRequest request, Long id) {
        User creator = getUserRequesting(request);
        return classroomRepo.findByIdAndCreator(id, creator);
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
        String code = generateClassCode();

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
    public Page<Classroom> getCreatedClassListByPageAndSearchName(HttpServletRequest request, Integer page, String searchName) {
        User creator = getUserRequesting(request);

        return classroomRepo.getAllClassroom(searchName, PageRequest.of(page, 5));
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
            learner.getClassrooms().add(classroom);
            userRepo.save(learner);
            return true;
        }
        return false;
    }

    @Override
    public User getUserRequesting(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal(); 
        String requestingUserByEmail = userPrincipal.getName();
        User requestingUser = userRepo.findByEmail(requestingUserByEmail);
        return requestingUser;
    }

    @Override
    public void removeMemberFromClassroom(Long classId, Long memberId, User requestingUser) {
        Classroom classroom = classroomRepo.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found with id: " + classId));

        if (!classroom.getCreator().getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Only the teacher in charge can remove members from the classroom.");
        }

        User memberToRemove = userRepo.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + memberId));

        if (classroom.getMembers().remove(memberToRemove)) {
            classroomRepo.save(classroom);
            memberToRemove.getClassrooms().remove(classroom);
            userRepo.save(memberToRemove);
        }
    }

    @Override
    public List<Classroom> getCreatedClassrooms(HttpServletRequest request) {
        User creator = getUserRequesting(request);
        return classroomRepo.findByCreator(creator);
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
