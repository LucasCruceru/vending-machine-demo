package com.example.vendingmachine.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int amountAvailable;
    private int cost;
    @ManyToOne
    private User user;

}
