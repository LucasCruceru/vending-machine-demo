package com.example.vendingmachine.controller;

import com.example.vendingmachine.dto.ProductUpdateRequestDto;
import com.example.vendingmachine.model.Product;
import com.example.vendingmachine.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "product")
public class ProductController {
    @Autowired
    private ProductService service;

    @GetMapping()
    public ResponseEntity<List<Product>> get() {
        return ResponseEntity.ok(service.get());
    }

    @PostMapping()
    public ResponseEntity<Product> create(@Valid @RequestBody Product request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping()
    public ResponseEntity<Product> update(@Valid @RequestBody ProductUpdateRequestDto product, @RequestParam long id) {
        return ResponseEntity.ok(service.update(product, id));
    }

    @DeleteMapping()
    public ResponseEntity<String> delete(@RequestParam long id) {
        service.delete(id);
        return ResponseEntity.ok("Product deleted!");
    }
}
