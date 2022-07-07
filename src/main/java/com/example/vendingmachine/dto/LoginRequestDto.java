package com.example.vendingmachine.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class LoginRequestDto {
    @NotBlank(message = "Username shouldn't be null")
    private String username;
    @NotBlank(message = "Password shouldn't be null")
    private String password;

}
