package com.group3.ezquiz.service.impl;

import com.group3.ezquiz.model.Option;
import com.group3.ezquiz.model.dto.UserDTO;
import com.group3.ezquiz.model.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group3.ezquiz.model.Role;
import com.group3.ezquiz.model.User;
import com.group3.ezquiz.payload.UserRequest;
import com.group3.ezquiz.repository.UserRepo;
import com.group3.ezquiz.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepo userRepo;

  @Override
  public User getUserRequesting(HttpServletRequest http) {

    Principal userPrincipal = http.getUserPrincipal();
    String email = userPrincipal.getName(); //
    return userRepo.findByEmail(email);
  }

  @Override
  public void registerUser(UserRequest regUser) {
    // Validation (include pass)
    // Encrypt password
    String encodedPass = passwordEncoder.encode(regUser.getPassword());

    // Save
    userRepo.save(
        User.builder()
            .email(regUser.getEmail())
            .fullName(regUser.getFullName())
            .password(encodedPass)
            .isEnable(false)
            .isVerified(false)
            .role(Role.LEARNER)
            .build());
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepo.findByEmail(email);
  }

  @Override
  public Page<User> getListUser(HttpServletRequest http,String email, Pageable page) {
    return userRepo.getAllUser(email, email, page);
  }

  @Override
  public UserDTO getUserById(Long id) {
    Optional<User> user = userRepo.findById(id);
    if(!user.isPresent()) {
      throw new UsernameNotFoundException("user not found");
    }
    return UserMapper.toUserDTO(user.get());
  }

  @Override
  public UserDTO update(UserDTO userDTO, Long id) {
    Optional<User> userOptional = userRepo.findById(id);
    if(!userOptional.isPresent()) {
      throw new UsernameNotFoundException("user not found");
    }
    User user = userOptional.get();
    user.setUpdateAt(Timestamp.valueOf(LocalDateTime.now()));
    user.setRole(userDTO.getRole());
    user.setEmail(userDTO.getEmail());
    user.setFullName(userDTO.getFullName());
    user.setIsEnable(userDTO.getIsEnable());
    user.setIsVerified(userDTO.getIsVerified());
    user.setPhone(userDTO.getPhone());
    user.setAvatar(userDTO.getAvatar());
    user.setNote(userDTO.getNote());
    userRepo.save(user);
    return UserMapper.toUserDTO(user);
  }

  @Override
  public void delete(Long id) {
    userRepo.deleteById(id);
  }
}
