package com.example.vendingmachine.controller;

import com.example.vendingmachine.dto.UserRequestDto;
import com.example.vendingmachine.model.User;
import com.example.vendingmachine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<User>> get(@RequestBody(required = false) List<String> usernames) {
        return ResponseEntity.ok(userService.get(usernames));
    }
    @PostMapping()
    public ResponseEntity<User> create(@Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(userService.create(request));
    }
    @PutMapping()
    public ResponseEntity<User> update(@Valid @RequestBody UserRequestDto userRequestDto,
                                       @RequestParam String username) {
        return ResponseEntity.ok(userService.update(userRequestDto, username));
    }
    @DeleteMapping()
    public ResponseEntity<String> delete(@RequestParam String username) {
        if (userService.delete(username))
            return ResponseEntity.ok("User deleted!");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
    }
}
