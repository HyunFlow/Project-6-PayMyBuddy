package com.openclassrooms.pay_my_buddy.web.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

  @NotBlank
  private String username;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(min = 8, max = 100)
  private String password;
}
