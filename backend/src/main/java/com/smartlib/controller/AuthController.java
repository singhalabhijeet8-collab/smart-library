package com.smartlib.controller;

import com.smartlib.dto.AuthRequest;
import com.smartlib.entity.User;
import com.smartlib.repository.UserRepository;
import com.smartlib.util.JwtUtil; // ✅ IMPORTANT (util, not security)

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*") // ✅ FIX CORS
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public User register(@RequestBody AuthRequest req) {
        User user = new User();
        user.setEmail(req.email);
        user.setPassword(req.password);
        user.setName("User");

        return userRepo.save(user);
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest req) {
        User user = userRepo.findByEmail(req.email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(req.password)) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return response;
    }
}