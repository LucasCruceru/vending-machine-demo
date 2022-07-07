package com.example.vendingmachine.service;

import com.example.vendingmachine.config.JWTToken;
import com.example.vendingmachine.config.TokenProvider;
import com.example.vendingmachine.dto.LoginRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@AllArgsConstructor
public class LoginService {

	private final TokenProvider tokenProvider;
	private final AuthenticationManager authenticationManager;

	public JWTToken login(LoginRequestDto loginDto, HttpServletResponse response) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
		Authentication authentication = this.authenticationManager.authenticate(authenticationToken);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.createToken(authentication);

		response.addHeader(TokenProvider.AUTHORIZATION_HEADER, "Bearer " + jwt);

		return new JWTToken(jwt);
	}

}
