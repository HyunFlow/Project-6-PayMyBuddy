package com.openclassrooms.pay_my_buddy.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
  private String email;
  private String password;

}
