package com.openclassrooms.pay_my_buddy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupDTO {
  private String username;
  private String email;
  private String password;
}
