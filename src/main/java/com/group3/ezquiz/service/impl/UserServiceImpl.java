package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.payload.UserDto;
import com.group3.ezquiz.payload.auth.RegisterRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.group3.ezquiz.service.EmailService;
import com.group3.ezquiz.service.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.group3.ezquiz.exception.ResourceNotFoundException;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.model.enums.Role;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

  private final String[] PERMITED_EMAIL_DOMAINS = { "gmail.com", "fpt.edu.vn" };

  private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UserRepo userRepo;

  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailService emailService;

  @Override
  public BindingResult registerUser(RegisterRequest regUser, BindingResult result) {

    FieldError emailError = validateEmail(regUser.getEmail());
    if (emailError != null && result.getFieldError("email") == null) {
      result.addError(emailError);
    }
    if (!result.hasErrors()) {
      log.info("Register info ok!");
      sendMailTo(regUser.getEmail());
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
    }
    return result;
  }

  @Override
  public User getByEmail(String email) {
    return userRepo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Not found email: " + email));
  }

  @Override
  public User getUserRequesting(HttpServletRequest http) {
    Principal userPrincipal = http.getUserPrincipal();
    String email = userPrincipal.getName();
    return getByEmail(email);
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
        // to update
        .role(user.getRole())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .password(encodedPass)
        .isVerified(user.getIsVerified())
        .isEnable(user.getIsEnable())
        .phone(user.getPhone())
        .note(user.getNote())
        .build();
    userRepo.save(saveUser);
  }

  @Override
  public void updatePassword(String email, String pass) {
    String encodedPass = passwordEncoder.encode(pass);
    User user = getByEmail(email);
    user.setPassword(encodedPass);
    userRepo.save(user);
  }

  @Override
  public boolean checkEmailExist(String email) {
    return userRepo.findByEmail(email).isPresent();
  }

  @Override
  public User findLearnerByEmail(String email) {
    return userRepo
        .findByEmailAndRole(email, Role.LEARNER)
        .orElse(null);
  }

  @Override
  public void verifyAccount(String email) {
    User byEmail = getByEmail(email);
    byEmail.setIsVerified(true);
    userRepo.save(byEmail);
  }

  private FieldError validateEmail(String email) {

    boolean permited = false;
    for (String domain : PERMITED_EMAIL_DOMAINS) {
      if (email.endsWith(domain)) {
        permited = true;
      }
    }
    if (!permited) {
      String invalidDomain = email.substring(email.indexOf('@') + 1);
      return new FieldError("RegisterRequest", "email", "'" + invalidDomain + "' is invalid domain!");
    }
    Boolean emailExisted = userRepo.findByEmail(email).isPresent();
    if (emailExisted) {
      return new FieldError("RegisterRequest", "email", "Email existed!");
    }
    return null;
  }

  private void sendMailTo(String email) {
    String subject = "ezQuiz Verify Account";
    Resource resource = new ClassPathResource("static/email/email-verify.html");
    Scanner scanner = null;
    try {
      scanner = new Scanner(resource.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String content = scanner.useDelimiter("\\A").next();
    scanner.close();
    String token = jwtService.generateToken(email);
    content = content.replace("[token]", token);
    try {
      emailService.sendSimpleMessage(email, subject, content);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

}
