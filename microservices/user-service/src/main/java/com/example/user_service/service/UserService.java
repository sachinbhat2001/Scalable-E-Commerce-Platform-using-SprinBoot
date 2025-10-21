package com.example.user_service.service;

import java.util.List;

import com.example.user_service.entity.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(Long id); // Changed to Long
    UserDTO getUserByUsername(String username);
    UserDTO getUserByEmail(String email);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UserDTO userDTO); // Changed to Long
    void deleteUser(Long id); // Changed to Long
    boolean userExists(Long id); // Changed to Long
}