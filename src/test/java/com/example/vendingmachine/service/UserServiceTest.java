package com.example.vendingmachine.service;

import com.example.vendingmachine.dto.UserRequestDto;
import com.example.vendingmachine.model.Role;
import com.example.vendingmachine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Test
    void get_shouldGetSingleUser() {
        String username = "username";
        var expected = Collections.singletonList(username);

        when(repository.getAllByUsernameIn(expected)).thenReturn(Collections.singletonList(TestMock.generateBuyerUser(username)));
        var result = service.get(expected);

        assertNotNull(result);
        assertEquals(result.size(), expected.size());
        assertEquals(result.get(0).getUsername(), expected.get(0));
    }

    @Test
    void get_shouldGetAllIfRequestNull() {
        String username1 = "username1";
        String username2 = "username2";
        String username3 = "username3";

        when(repository.findAll()).thenReturn(List.of(
                TestMock.generateBuyerUser(username1),
                TestMock.generateBuyerUser(username2),
                TestMock.generateBuyerUser(username3)));
        var result = service.get(null);

        assertNotNull(result);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getUsername(), username1);
        assertEquals(result.get(1).getUsername(), username2);
        assertEquals(result.get(2).getUsername(), username3);
    }


    @Test
    void get_shouldGetMultipleUsers() {
        String username1 = "username1";
        String username2 = "username2";
        String username3 = "username3";
        var expected = List.of(username1,username2,username3);

        when(repository.getAllByUsernameIn(expected)).thenReturn(List.of(
                TestMock.generateBuyerUser(username1),
                TestMock.generateBuyerUser(username2),
                TestMock.generateBuyerUser(username3)));
        var result = service.get(expected);

        assertNotNull(result);
        assertEquals(result.size(), expected.size());
        assertEquals(result.get(0).getUsername(), expected.get(0));
        assertEquals(result.get(1).getUsername(), expected.get(1));
        assertEquals(result.get(2).getUsername(), expected.get(2));
    }

    @Test
    void get_shouldGetAllUsersIfDoesntFindAny() {
        String username1 = "username1";
        String username2 = "username2";
        String username3 = "username3";
        var expected = Collections.singletonList("inexistentUser");

        when(repository.getAllByUsernameIn(expected)).thenReturn(Collections.emptyList());
        when(repository.findAll()).thenReturn(List.of(
                TestMock.generateBuyerUser(username1),
                TestMock.generateBuyerUser(username2),
                TestMock.generateBuyerUser(username3)));
        var result = service.get(expected);

        assertNotNull(result);
        assertNotEquals(result.size(), expected.size());
        assertEquals(result.get(0).getUsername(), username1);
        assertEquals(result.get(1).getUsername(), username2);
        assertEquals(result.get(2).getUsername(), username3);
    }

    @Test
    void create_UserAlreadyExists() {
        var username = "username";
        var request = new UserRequestDto();
        request.setUsername(username);
        request.setPassword("password");
        request.setRole("Buyer");

        when(repository.getByUsername(username)).thenReturn(Optional.of(TestMock.generateBuyerUser(username)));
        var exception  = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals("User already exists!", exception.getReason());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void create_UserCreated() {
        var username = "username";
        var request = new UserRequestDto();
        request.setUsername(username);
        request.setPassword("password");
        request.setRole("Buyer");

        when(repository.getByUsername(username)).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any())).thenReturn(TestMock.generateBuyerUser(username));
        var response = service.create(request);

        assertEquals(response.getUsername(), request.getUsername());
        assertEquals(response.getPassword(), request.getPassword());
        assertEquals(response.getRole(), Role.BUYER);
    }

    @Test
    void delete_UserNotFound() {
        var username = "username";

        when(repository.deleteByUsername(username)).thenReturn(0);
        var response = service.delete(username);

        assertFalse(response);
    }

    @Test
    void delete_UserDeleted() {
        var username = "username";

        when(repository.deleteByUsername(username)).thenReturn(1);
        var response = service.delete(username);

        assertTrue(response);
    }

    @Test
    void update_NewUsernameAlreadyExists() {
        var username = "username";
        var request = new UserRequestDto();
        request.setUsername("newUsername");
        request.setPassword("password");
        request.setRole("Buyer");

        when(repository.getByUsername(any())).thenReturn(Optional.of(TestMock.generateBuyerUser(username)));
        var exception  = assertThrows(ResponseStatusException.class, () -> service.update(request, username));

        assertEquals("New username already exists!", exception.getReason());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void update_UserDoesntExists() {
        var username = "username";
        var request = new UserRequestDto();
        request.setUsername("newUsername");
        request.setPassword("password");
        request.setRole("Buyer");

        when(repository.getByUsername(username)).thenReturn(Optional.empty());
        var exception  = assertThrows(ResponseStatusException.class, () -> service.update(request, username));

        assertEquals("User not found!", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void update_UserUpdates() {
        var username = "username";
        var request = new UserRequestDto();
        request.setUsername("newUsername");
        request.setPassword("password");
        request.setRole("Buyer");

        var expected = TestMock.generateBuyerUser(username);

        when(repository.getByUsername(username)).thenReturn(Optional.of(expected));
        when(repository.saveAndFlush(any())).thenReturn(TestMock.generateBuyerUser(request.getUsername()));
        var response = service.update(request, username);

        assertEquals(response.getUsername(), request.getUsername());
        assertEquals(response.getPassword(), request.getPassword());
        assertEquals(response.getRole(), Role.BUYER);
    }

    @Test
    void loadUserByUsername_shouldReturnValidObject() {
        var username = "username";
        var request = new UserRequestDto();
        request.setUsername(username);
        request.setPassword("password");
        request.setRole("Buyer");

        when(repository.getByUsername(username)).thenReturn(Optional.of(TestMock.generateBuyerUser(username)));

        var response = service.loadUserByUsername(username);

        assertEquals(response.getUsername(), request.getUsername());
        assertEquals(response.getPassword(), request.getPassword());
        assertEquals(response.getAuthorities().size(),1);
    }

    @Test
    void loadUserByUsername_shouldThrowException() {
        var username = "username";
        var request = new UserRequestDto();
        request.setUsername(username);
        request.setPassword("password");
        request.setRole("Buyer");

        when(repository.getByUsername(username)).thenReturn(Optional.empty());

        var exception  = assertThrows(UsernameNotFoundException.class, () ->  service.loadUserByUsername(username));

        assertEquals("User not found!", exception.getMessage());
    }
}