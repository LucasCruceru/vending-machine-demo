package com.example.vendingmachine.repository;

import com.example.vendingmachine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getByUsername(String username);
    List<User> getAllByUsernameIn(List<String> username);
    Integer deleteByUsername(String username);

}
