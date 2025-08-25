package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the specified username and password.
     * <p>
     * This method creates a new user account after verifying that the username
     * is unique in the system. If the username already exists, an exception is thrown.
     * </p>
     *
     * @param username the desired username for the new user account
     * @param password the password for the new user account that will be hashed before storage
     * @return the newly created and saved User entity
     * @throws IllegalArgumentException if the username already exists in the database
     * @throws IllegalArgumentException if username or password is null or empty
     */
    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, hashedPassword);

        return userRepository.save(user);
    }

    /**
     * Authenticates a user with the provided username and password.
     * <p>
     * This method attempts to find a user with the given username and verifies
     * the provided password against the stored hashed password using BCrypt.
     * If authentication succeeds, the user entity is returned wrapped in an Optional.
     * If authentication fails, an empty Optional is returned.
     * </p>
     *
     * @param username the username of the user attempting to log in
     * @param password the plain-text password provided for authentication
     * @return an Optional containing the authenticated User if login succeeds,
     *         or an empty Optional if authentication fails
     * @throws IllegalArgumentException if username or password is null
     */
    public Optional<User> loginUser(String username, String password) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));

        if (user.isPresent() &&  passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        } else {
            return Optional.empty();
        }
    }
}
