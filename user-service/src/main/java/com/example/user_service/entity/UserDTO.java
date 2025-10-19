package com.example.user_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id; // Changed to Long
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}