package com.example.vendingmachine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("application")
@Getter
@Setter
public class ApplicationProperties {
	@Value("${auth.secret}")
	private String secret;
	@Value("${auth.seconds-valid}")
	private long secondsValid;
}