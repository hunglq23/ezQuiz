package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.model.Classroom;
import com.group3.ezquiz.model.QuizUUID;
import com.group3.ezquiz.payload.ObjectDto;
import com.group3.ezquiz.payload.UserDto;

import com.group3.ezquiz.repository.ClassroomRepo;
import com.group3.ezquiz.repository.QuizUUIDRepo;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.exception.InvalidEmailException;
import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.Role;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.MessageResponse;
import com.group3.ezquiz.payload.RegisterRequest;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepo userRepo;
  private final QuizUUIDRepo quizUUIDRepo;
  private final ClassroomRepo classroomRepo;

  @Override
  public User getUserRequesting(HttpServletRequest http) {

    Principal userPrincipal = http.getUserPrincipal();
    String email = userPrincipal.getName(); //
    return userRepo.findByEmail(email);
  }

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
        new MessageResponse("New account was created successfully!"));
  }

  private void validateEmail(String email) {
    User byEmail = userRepo.findByEmail(email);
    if (byEmail != null) { // email existed
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
  public User getUserByEmail(String email) {
    return userRepo.findByEmail(email);
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
    User userById = userRepo.findUserById(id);
    if (userById != null) {
      // if (userById.getUpdatedBy() == null) {
      // userById.setUpdateAt(null);
      // }
      return userById;
    }
    throw new ResourceNotFoundException("Cannot find user with" + id);
  }

  @Override
  public void update(HttpServletRequest request, UserDto user, Long id) {
    String encodedPass = passwordEncoder.encode(user.getPassword());
    User userRequesting = getUserRequesting(request);
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
  public List<ObjectDto> getQuizAndClassroomByTeacher(HttpServletRequest request, Boolean sortOrder) {
    User userRequesting = getUserRequesting(request);
    List<QuizUUID> quizByUser = quizUUIDRepo.findByCreator(userRequesting);
    List<Classroom> classroomByUser = classroomRepo.findByCreator(userRequesting);

    List<ObjectDto> objectDtoList = Stream.concat(
                    quizByUser.stream().map(this::createQuizObjectDto),
                    classroomByUser.stream().map(this::createClassroomObjectDto))
            .collect(Collectors.toList());

    Comparator<ObjectDto> comparator = Comparator.comparing(ObjectDto::getTimeString);
    if (sortOrder) {
      comparator = comparator.reversed();
    }
    objectDtoList.sort(comparator);

    objectDtoList.forEach(objectDto -> objectDto.setTimeString(
            calculateTimeElapsed(
                    convertStringToTimestamp(objectDto.getTimeString(), "yyyy-MM-dd HH:mm:ss.SSSSSS"))));

    return objectDtoList;
  }

  private ObjectDto createQuizObjectDto(QuizUUID quiz) {
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


  public static Timestamp convertStringToTimestamp(String dateString, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
    return Timestamp.valueOf(dateTime);
  }
  public static String calculateTimeElapsed(Timestamp creationTime) {
    Instant instant = creationTime.toInstant();
    Instant currentInstant = Instant.now();
    Duration duration = Duration.between(instant, currentInstant);

    long seconds = duration.getSeconds();

    long days = seconds / (24 * 3600);
    seconds = seconds % (24 * 3600);
    long hours = seconds / 3600;
    seconds %= 3600;
    long minutes = seconds / 60;
    seconds %= 60;

    StringBuilder timeElapsedStringBuilder = new StringBuilder();
    if (days > 0) {
      return days + " day(s) ago";
    }
    if (hours > 0) {
      return hours + " hour(s) ago";
    }
    if (minutes > 0) {
      return minutes + " minute(s) ago";
    }
    return seconds + " second(s) ago";
  }
}
