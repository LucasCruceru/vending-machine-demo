package com.example.vendingmachine.controller;

import com.example.vendingmachine.config.JWTToken;
import com.example.vendingmachine.dto.LoginRequestDto;
import com.example.vendingmachine.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class AuthController {

	private final LoginService loginService;

	@PostMapping()
	public ResponseEntity<JWTToken> login(@Valid @RequestBody LoginRequestDto loginDto, HttpServletResponse response) {
		return ResponseEntity.ok(loginService.login(loginDto, response));
	}

}