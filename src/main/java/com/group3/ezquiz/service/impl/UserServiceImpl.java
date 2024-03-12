package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.Quiz;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.ObjectDto;
import com.group3.ezquiz.payload.UserDto;
import com.group3.ezquiz.payload.auth.RegisterRequest;

import com.group3.ezquiz.payload.quiz.QuizDto;
import com.group3.ezquiz.repository.ClassroomRepo;
import com.group3.ezquiz.repository.QuizRepo;
import com.group3.ezquiz.utils.Utility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.InvalidEmailException;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Role;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepo userRepo;
  private final QuizRepo quizRepo;
  private final ClassroomRepo classroomRepo;

  @Override
  public ResponseEntity<?> registerUser(RegisterRequest regUser) {

    validateEmail(regUser.getEmail());

    String encodedPass = passwordEncoder.encode(regUser.getPassword());

    userRepo.save(
        User.builder()
            .email(regUser.getEmail())
            .fullName(regUser.getFullName())
            .password(encodedPass)
            .isEnable(true)
            .isVerified(false)
            .role(Role.LEARNER)
            .build());

    return ResponseEntity.ok(
        MessageResponse.builder()
            .message("Your account was created successfully!")
            .timestamp(LocalDateTime.now())
            .build());
  }

  private void validateEmail(String email) {
    Boolean emailExisted = userRepo.findByEmail(email).isPresent();
    if (emailExisted) {
      throw new InvalidEmailException("Email existed!");
    } else {
      String[] permitedEmailDomains = { "@gmail.com", "@fpt.edu.vn", "@email" };
      boolean permited = false;
      for (String domain : permitedEmailDomains) {
        if (email.endsWith(domain)) {
          permited = true;
        }
      }
      if (!permited) {
        String invalidDomain = email.substring(email.indexOf('@') + 1);
        throw new InvalidEmailException("'" + invalidDomain + "' is invalid domain!");
      }
    }
  }

  @Override
  public User getUserRequesting(HttpServletRequest http) {

    Principal userPrincipal = http.getUserPrincipal();
    String email = userPrincipal.getName();
    return getUserByEmail(email);
  }

  @Override
  public Page<User> getListUser(HttpServletRequest http, String email, Boolean status, Pageable page) {
    return userRepo.getAllUser(email, email, status, page);
  }

  @Override
  public void createUser(HttpServletRequest request, UserDto userDto) {
    String encodedPass = passwordEncoder.encode(userDto.getPassword());
    userRepo.save(
        User.builder()
            .role(userDto.getRole())
            .email(userDto.getEmail())
            .fullName(userDto.getFullName())
            .password(encodedPass)
            .isVerified(userDto.getIsVerified())
            .isEnable(userDto.getIsEnable())
            .phone(userDto.getPhone())
            .note(userDto.getNote())
            .build());
  }

  @Override
  public User getUserById(Long id) {

    return userRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found user ID: " + id));
  }

  @Override
  public void update(HttpServletRequest request, UserDto user, Long id) {
    String encodedPass = passwordEncoder.encode(user.getPassword());
    User existedUser = getUserById(id);
    User saveUser = User.builder()
        // unchangeable
        .id(existedUser.getId())
        .createdAt(existedUser.getCreatedAt())
        // .createdBy(existedUser.getCreatedBy())
        // to update
        .role(user.getRole())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .password(encodedPass)
        .isVerified(user.getIsVerified())
        .isEnable(user.getIsEnable())
        .phone(user.getPhone())
        .note(user.getNote())
        // .updatedBy(userRequesting.getId())
        .build();
    userRepo.save(saveUser);
  }

  @Override
  public void delete(Long id) {
    userRepo.deleteById(id);
  }

  @Override
  public Page<ObjectDto> getQuizAndClassroomByTeacher(
          HttpServletRequest http,
          String sortOrder,
          Pageable pageable) {
    User userRequesting = getUserRequesting(http);
    List<Quiz> quizByUser = quizRepo.findByCreator(userRequesting);
    List<Classroom> classroomByUser = classroomRepo.findByCreator(userRequesting);
    List<ObjectDto> objectDtoList = Stream.concat(
                    quizByUser.stream().map(this::createQuizObjectDto),
                    classroomByUser.stream().map(this::createClassroomObjectDto))
            .collect(Collectors.toList());
    Comparator<ObjectDto> comparator;
    switch (sortOrder){
      case "latest":
        comparator = Comparator.comparing(ObjectDto::getTimeString).reversed();
        objectDtoList.sort(comparator);
        break;
      case "oldest":
        comparator = Comparator.comparing(ObjectDto::getTimeString);
        objectDtoList.sort(comparator);
        break;
    }
    objectDtoList.forEach(objectDto -> objectDto.setTimeString(
            Utility.calculateTimeElapsed(
                    Utility.convertStringToTimestamp(objectDto.timeString(), "yyyy-MM-dd HH:mm:ss"))));
    int pageSize = pageable.getPageSize();
    int currentPage = pageable.getPageNumber();
    int startItem = currentPage * pageSize;
    List<ObjectDto> pagedObjectDtoList;
    int toIndex = Math.min(startItem + pageSize, objectDtoList.size());
    pagedObjectDtoList = objectDtoList.subList(startItem, toIndex);
    return new PageImpl<>(pagedObjectDtoList, PageRequest.of(currentPage, pageSize), objectDtoList.size());
  }

  private ObjectDto createQuizObjectDto(Quiz quiz) {
    return ObjectDto.builder()
            .type("Quiz")
            .name(quiz.getTitle())
            .description(quiz.getDescription())
            .image(quiz.getImageUrl())
            .isDraft(quiz.getIsDraft())
            .itemNumber(quiz.getQuestions().size())
            .timeString(quiz.getCreatedAt().toString())
            .build();
  }

  private ObjectDto createClassroomObjectDto(Classroom classroom) {
    return ObjectDto.builder()
            .type("Classroom")
            .name(classroom.getClassName())
            .description(classroom.getDescription())
            .image(classroom.getImageURL())
            .isDraft(classroom.getIsDraft())
            .itemNumber(classroom.getMembers().size())
            .timeString(classroom.getCreatedAt().toString())
            .build();
  }

  private User getUserByEmail(String email) {
    return userRepo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(email));
  }

}
