package com.group3.ezquiz.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.group3.ezquiz.exception.InvalidClassroomException;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.ClassJoining;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.LibraryReqParam;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.classroom.ClassroomDetailDto;
import com.group3.ezquiz.payload.classroom.ClassroomDto;
import com.group3.ezquiz.repository.ClassroomRepo;
import com.group3.ezquiz.service.IClassroomService;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements IClassroomService {

  private final static Logger log = LoggerFactory.getLogger(ClassroomServiceImpl.class);
  private final Integer MAX_MEMBER_PER_CLASS = 30;
  private final ClassroomRepo classroomRepo;
  private final IUserService userService;

  @Override
  public ResponseEntity<?> createClass(HttpServletRequest request, ClassroomDetailDto dto) {
    User creator = userService.getUserRequesting(request);
    if (classroomRepo.findByCreatorAndName(creator, dto.getName()).isPresent()) {
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

  @Override
  public Classroom getClassroomByRequestAndId(HttpServletRequest request, Long id) {
    User creator = userService.getUserRequesting(request);
    return classroomRepo.findByIdAndCreator(id, creator)
        .orElseThrow(() -> new ResourceNotFoundException("Not found classroom"));
  }

  @Override
  public Classroom updateClassroom(Long id, Classroom updatedClassroom) {
    Optional<Classroom> optional = classroomRepo.findById(id);

    if (optional.isPresent()) {
      Classroom classroomToUpdate = optional.get();

      classroomToUpdate.setName(updatedClassroom.getName());
      classroomToUpdate.setDescription(updatedClassroom.getDescription());

      classroomRepo.save(classroomToUpdate);
      System.out.println("Classroom updated sucessfully");
      return classroomToUpdate;
    } else {
      throw new RuntimeException("Classroom not found for id: " + id);
    }
  }

  @Override
  public void deleteClassById(Long id) {
    Classroom existClassroom = classroomRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found class ID: " + id));
    if (existClassroom != null) {
      classroomRepo.delete(existClassroom);
    }
  }

  @Override
  public boolean joinClassroom(HttpServletRequest request, String code) {
    User learner = userService.getUserRequesting(request);
    Classroom classroom = classroomRepo.findByCode(code);
    ClassJoining classJoining = new ClassJoining();
    if (classroom != null && learner != null) {
      classJoining.setLearner(learner);
      classJoining.setClassroom(classroom);
      classroom.getClassJoinings().add(classJoining);
      classroomRepo.save(classroom);
      return true;
    }
    return false;
  }

  // public void removeLearnerFromClassroomLearnerId(Classroom classroom, Long
  // learnerId) {

  // List<ClassJoining> classJoinings = classroom.getClassJoinings();
  // classJoinings.removeIf(joining ->
  // joining.getLearner().getId().equals(learnerId));
  // classroomRepo.save(classroom);
  // }

  @Override
  public void importClassroomDataFromExcel(HttpServletRequest request, MultipartFile file) {
    User userRequesting = userService.getUserRequesting(request);

    List<Classroom> classrooms = new ArrayList<>();

    try (InputStream inputStream = file.getInputStream()) {
      Workbook workbook = WorkbookFactory.create(inputStream);

      for (Sheet sheet : workbook) { // loop through all the sheet
        Classroom newClassroom = processSheet(
            sheet,
            Classroom.builder()
                .creator(userRequesting)
                .isEnable(true)
                .code(generateClassCode())
                .build());
        classrooms.add(newClassroom);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public boolean importLearnerDataFromExcel(MultipartFile excelFile, Classroom classroom) {
    try (InputStream inputStream = excelFile.getInputStream()) {
      Workbook workbook = WorkbookFactory.create(inputStream);
      classroom = processSheet(workbook.getSheetAt(0), classroom); // only get the first sheet

    } catch (IOException e) {
      e.printStackTrace();
    }
    return classroom != null;

  }

  @Override
  public Page<ClassroomDto> getCreatedClassrooms(HttpServletRequest request, LibraryReqParam params) {

    User userRequest = userService.getUserRequesting(request);
    Page<Classroom> classroomPage = classroomRepo
        .findByCreatorAndNameContaining(
            userRequest,
            params.getSearch(),
            PageRequest.of(params.getPage() - 1,
                params.getSize(),
                params.getSortType()));

    return classroomPage.map(this::mapToClassroomDto);
  }

  @Override
  public List<Classroom> findClassroomsByCreatorId(Long creatorId) {
    return classroomRepo.findByCreatorId(creatorId);
  }

  private Classroom processSheet(Sheet sheet, Classroom classroom) {
    List<ClassJoining> classJoinings = new ArrayList<>();

    Iterator<Row> rowIterator = sheet.iterator();

    // Skip the the first and second row
    rowIterator.next();
    rowIterator.next();

    if (classroom.getName() == null && rowIterator.hasNext()) { // Check the 3rd row,
      // which contains the name and desc of the classroom
      Row thirdRow = rowIterator.next();
      // Get the classroom name at the 5th cell of the third row
      String classroomName = (thirdRow.getCell(4).getStringCellValue()).trim();
      log.info("Class name: " + classroomName);
      classroom.setName(classroomName);

      if (thirdRow.getCell(5) != null) { // Check the description at the 6th cell exist or not
        // Get the classroom name at the 6th cell of the third row
        String description = (thirdRow.getCell(5).getStringCellValue()).trim();
        log.info("Description: " + description);
        classroom.setDescription(description);
      }

    } else if (rowIterator.hasNext()) {
      // skip the third row
      classJoinings = classroom.getClassJoinings();
    }

    int curMemberNum = 1;
    // Loop through all the remain rows
    while (rowIterator.hasNext() && curMemberNum <= MAX_MEMBER_PER_CLASS) {
      Row currentRow = rowIterator.next();

      // Get the student email at the 3th cell of the current row
      String email = (currentRow.getCell(2).getStringCellValue()).trim();
      log.info("Student email: " + email);
      User learnerByEmail = userService.findLearnerByEmail(email);
      ClassJoining classJoining = null;
      if (learnerByEmail != null) {
        classJoining = ClassJoining.builder()
            .learner(learnerByEmail)
            .build();

        // Get the learner displayed name at the 2th cell of the current row
        String displayedName = (currentRow.getCell(1).getStringCellValue()).trim();
        if (displayedName != null && !displayedName.isEmpty()) {
          classJoining.setLearnerDisplayedName(displayedName);
        }

        // Get the student phone at the 4th cell of the current row
        String displayedPhone = (currentRow.getCell(3).getStringCellValue()).trim();
        if (displayedPhone != null && !displayedPhone.isEmpty()) {
          classJoining.setLearnerDisplayedPhone(displayedPhone.substring(1));
        }

        if (classJoining != null) {
          classJoining.setClassroom(classroom);
          classJoinings.add(classJoining);
        }
      }

      curMemberNum++;
    }
    classroom.setClassJoinings(classJoinings);
    Classroom saved = classroomRepo.save(classroom);
    return saved;
  }

  private String generateClassCode() {
    UUID codeUUID = UUID.randomUUID();
    String codeClass = codeUUID.toString().replace("-", "").substring(0, 8).toUpperCase();
    return codeClass;
  }

  private ClassroomDto mapToClassroomDto(Classroom classroom) {
    return ClassroomDto.builder()
        .id(classroom.getId())
        .name(classroom.getName())
        .description(classroom.getDescription())
        .imageUrl(classroom.getImageURL())
        .itemNumber(classroom.getClassJoinings().size())
        .timestamp(classroom.getCreatedAt())
        .build();
  }

}
