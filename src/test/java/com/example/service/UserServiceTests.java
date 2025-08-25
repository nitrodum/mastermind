package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository = org.mockito.Mockito.mock(UserRepository.class);
        passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    public void testRegisterUserSuccess() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        User savedUser = new User("testUser", "hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User resultUser = userService.registerUser("testUser", "password");

        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(argThat(u ->
                u.getUsername().equals("testUser") &&
                        u.getPassword().equals("hashedPassword")));
        assertEquals("testUser", resultUser.getUsername());
        assertEquals("hashedPassword", resultUser.getPassword());
    }

    @Test
    public void testLoginUserSuccess() {
        User user = new User("testUser", "hashedPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);

        Optional<User> loggedUser = userService.loginUser("testUser", "password");

        verify(userRepository, times(1)).findByUsername("testUser");
        verify(passwordEncoder, times(1)).matches("password", "hashedPassword");
        assertTrue(loggedUser.isPresent());
        assertEquals("testUser", loggedUser.get().getUsername());
        assertEquals("hashedPassword", loggedUser.get().getPassword());
    }

    @Test
    public void testRegisterUserFailure_UserExists() {
        User existingUser = new User("existingUser", "hashedPassword");
        when(userRepository.findByUsername("existingUser")).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("existingUser", "newPassword"));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("existingUser");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    public void testLoginUserFailure_WrongPassword() {
        User user = new User("testUser", "hashedPassword");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        Optional<User> loggedUser = userService.loginUser("testUser", "wrongPassword");

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(passwordEncoder, times(1)).matches("wrongPassword", "hashedPassword");
        assertFalse(loggedUser.isPresent());
    }
}
