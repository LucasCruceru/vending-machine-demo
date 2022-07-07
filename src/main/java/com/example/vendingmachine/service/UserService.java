package com.example.vendingmachine.service;

import com.example.vendingmachine.dto.UserRequestDto;
import com.example.vendingmachine.model.Role;
import com.example.vendingmachine.model.User;
import com.example.vendingmachine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    public List<User> get(List<String> usernames) {
        if (usernames == null)
            return repository.findAll();

        List<User> foundUsers = repository.getAllByUsernameIn(usernames);
        if (!foundUsers.isEmpty()){
            return foundUsers;
        }
        return repository.findAll();
    }

    public User create(UserRequestDto request) {
        var requestedUser = repository.getByUsername(request.getUsername());
        if (requestedUser.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists!");

        return repository.saveAndFlush(createUserEntity(request));
    }

    public User update(UserRequestDto requestDto, String username) {

        var user = getUserByUsername(username);

        if(!username.equals(requestDto.getUsername()) && repository.getByUsername(requestDto.getUsername()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New username already exists!");

        updateUserEntity(requestDto, user);
        return repository.saveAndFlush(user);

    }

    @Transactional
    public boolean delete(String username) {
        return repository.deleteByUsername(username) == 1;

    }

    private User createUserEntity(UserRequestDto user) {
        User userToBePersisted = new User();
        updateUserEntity(user, userToBePersisted);
        return userToBePersisted;
    }

    private void updateUserEntity(UserRequestDto requestDto, User user) {
        user.setUsername(requestDto.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(requestDto.getPassword()));
        user.setRole(Role.valueOf(requestDto.getRole().toUpperCase()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.getByUsername(username);
        if (user.isEmpty())
            throw new UsernameNotFoundException("User not found!");


        return new org.springframework.security.core.userdetails.User(user.get().getUsername(),
                user.get().getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.get().getRole().toString())));
    }
    public User getUserByUsername(String username) {
        return repository.getByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
    }
}
