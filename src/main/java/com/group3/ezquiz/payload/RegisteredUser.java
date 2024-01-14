package com.group3.ezquiz.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredUser {
  private String email;
  private String fullName;
  private String password;
  private String passwordConfirm;
}
