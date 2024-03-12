package com.group3.ezquiz.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.group3.ezquiz.exception.InvalidClassroomException;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.ClassroomDto;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.repository.ClassroomRepo;
import com.group3.ezquiz.service.IClassroomService;
import com.group3.ezquiz.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements IClassroomService {

  private final Integer MAX_MEMBER_PER_CLASS = 30;
  private final static Logger log = LoggerFactory.getLogger(ClassroomServiceImpl.class);
  private final ClassroomRepo classroomRepo;
  private final IUserService userService;

  @Override
  public List<Classroom> getCreatedClassroomList(HttpServletRequest request) {
    User creator = userService.getUserRequesting(request);
    return classroomRepo.findByCreator(creator);
  }

  @Override
  public ResponseEntity<?> createClass(HttpServletRequest request, ClassroomDto dto) {
    User creator = userService.getUserRequesting(request);
    if( classroomRepo.findByCreatorAndName(creator, dto.getName()).isPresent()){
       throw new InvalidClassroomException("Classroom name existed!");
    }
    Classroom classroom = Classroom.builder()
        .name(dto.getName())
        .description(dto.getDescription())
        .code(generateClassCode())
        .creator(userService.getUserRequesting(request))
        .isEnable(true)
        .build();
    if (classroom != null) {
      classroomRepo.save(classroom);
      return new ResponseEntity<>(
          MessageResponse.builder()
              .message("Created class successfully!")
              .timestamp(LocalDateTime.now())
              .build(),
          HttpStatus.OK);
    }
    return new ResponseEntity<>(
        MessageResponse.builder()
            .message("Created fail!")
            .timestamp(LocalDateTime.now())
            .build(),
        HttpStatus.BAD_REQUEST);
  };

  private String generateClassCode() {
    UUID codeUUID = UUID.randomUUID();
    String codeClass = codeUUID.toString().replace("-", "").substring(0, 8).toUpperCase();
    return codeClass;
  }

}
