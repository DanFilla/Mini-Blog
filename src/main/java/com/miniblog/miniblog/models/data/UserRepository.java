package com.miniblog.miniblog.models.data;

import com.miniblog.miniblog.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findById(int id);
}
