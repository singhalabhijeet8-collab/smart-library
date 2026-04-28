package com.smartlib.controller;

import com.smartlib.entity.User;
import com.smartlib.service.UserService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public User register(@RequestBody User u) {
        return service.register(u);
    }

    @PostMapping("/login")
    public User login(@RequestBody User u) {
        return service.login(u.getEmail(), u.getPassword());
    }
}
