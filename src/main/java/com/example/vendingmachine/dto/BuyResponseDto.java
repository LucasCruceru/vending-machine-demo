package com.example.vendingmachine.dto;

import lombok.Data;

import java.util.List;

@Data
public class BuyResponseDto {
	private String productName;
	private Integer totalSpent;
	private List<Integer> change;
}