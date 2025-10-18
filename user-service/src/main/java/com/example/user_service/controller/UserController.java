package com.example.user_service.controller;

import com.example.user_service.JwtService.JwtTokenService;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    public UserController(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload){
        String email = payload.get("email");
        String password = payload.get("password");
        String fullName = payload.get("fullName");
        System.out.println("Register payload: " + payload);
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body("Email already exists");
        }
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .build();
        userRepository.save(user);

        return(ResponseEntity.ok(Map.of("id", user.getId()))) ;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload){

        String email = payload.get("email");
        String password = payload.get("password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtTokenService.generateToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(Map.of("token", token));

    }
}
