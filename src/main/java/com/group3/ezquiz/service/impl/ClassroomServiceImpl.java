package com.group3.ezquiz.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.ClassJoining;
import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.ClassroomDto;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.repository.ClassroomRepo;
import com.group3.ezquiz.repository.UserRepo;
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
    log.info("hi");
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
    if (classroom != null && learner != null) {
      ClassJoining classJoining = ClassJoining.builder()
          .learner(learner)
          .classroom(classroom)
          .learnerDisplayedName(learner.getFullName()) 
          .learnerDisplayedPhone(learner.getPhone())
          .build();

      classroom.getClassJoinings().add(classJoining);
      classroomRepo.save(classroom);
      learner.getClassJoinings().add(classJoining);
      userRepo.save(learner);
      return true;
    }
    return false;
  }

  @Override
  public Classroom removeMemberFromClassroomByMemberId(Classroom classroom, Long memberId) {
    // User member = userRepo.findUserById(memberId);
    // member.getJoinedClassrooms().remove(classroom);
    // userRepo.save(member);
    // classroom.getMembers().remove(member);
    return classroomRepo.save(classroom);

  }

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
      rowIterator.next(); // skip the third row
      classJoinings = classroom.getClassJoinings();
    }

    int curMemberNum = 1;
    // Loop through all the remain rows
    while (rowIterator.hasNext() && curMemberNum <= MAX_MEMBER_PER_CLASS) {
      Row currentRow = rowIterator.next();

      // Get the student email at the 3th cell of the current row
      String email = (currentRow.getCell(2).getStringCellValue()).trim();
      log.info("Student email: " + email);
      User userByEmail = userService.findUserByEmail(email);
      ClassJoining classJoining = null;
      if (userByEmail != null) {
        classJoining = ClassJoining.builder()
            .learner(userByEmail)
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
      }
      if (classJoining != null) {
        classJoinings.add(classJoining);
      }
      curMemberNum++;
    }
    classroom.setClassJoinings(classJoinings);
    Classroom saved = classroomRepo.save(classroom);
    return saved;

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

}
