package com.example.vendingmachine.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
@Data
public class ProductUpdateRequestDto {
    @NotBlank
    @NotEmpty
    private String name;
    private Integer cost;
    private Integer amountAvailable;
}
