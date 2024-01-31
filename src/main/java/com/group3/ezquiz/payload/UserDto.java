package com.group3.ezquiz.payload;

import com.group3.ezquiz.model.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull(message = "role can't be empty!")
    private Role role;
    @NotEmpty(message = "email can't be empty!")
    @Email(message = "Email is not valid")
    private String email;
    @NotEmpty(message = "password can't be empty!")
    private String password;
    @NotEmpty(message = "full name can't be empty!")
    private String fullName;
    private Boolean isEnable;
    private Boolean isVerified;
    @Size(max = 10, min = 10, message = "Invalid phone number")
    @Pattern(regexp = "^[0-9]+$", message = "Only digits are allowed")
    private String phone;
    @Size(max = 250, message = "Exceed 250 characters")
    private String note;

}