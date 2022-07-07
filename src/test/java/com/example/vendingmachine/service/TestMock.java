package com.example.vendingmachine.service;

import com.example.vendingmachine.model.Product;
import com.example.vendingmachine.model.User;
import com.example.vendingmachine.model.Role;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestMock {

    User generateBuyerUser(String username) {
        var user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setRole(Role.BUYER);
        user.setDeposit(5);
        return user;
    }

    Product generateProduct() {
        var product = new Product();
        product.setName("product");
        product.setAmountAvailable(2);
        product.setCost(20);
        product.setUser(generateBuyerUser("productUser"));
        return product;
    }
}
