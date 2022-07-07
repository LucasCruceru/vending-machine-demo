package com.example.vendingmachine.repository;

import com.example.vendingmachine.model.Product;
import com.example.vendingmachine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndUser(long id, User user);
}
