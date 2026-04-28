package com.smartlib.service;

import com.smartlib.entity.User;
import com.smartlib.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User register(User u) {
        return repo.save(u);
    }

    public User login(String email, String pass) {
        return repo.findByEmail(email)
                .filter(u -> u.getPassword().equals(pass))
                .orElse(null);
    }
}
