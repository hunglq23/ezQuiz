package com.group3.ezquiz.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserRequest {
  private String email;
  private String fullName;
  private String password;
  private String passwordConfirm;
}
