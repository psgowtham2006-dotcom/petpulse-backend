package com.petpulse.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.petpulse.dto.RegisterRequest;
import com.petpulse.entity.User;
import com.petpulse.repository.UserRepository;
import com.petpulse.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthControllerTest {

    @Test
    void registerUserShouldNormalizeRolesWithoutRolePrefix() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);
        AuthController controller = new AuthController(authenticationManager, userRepository, encoder, jwtUtils);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("shelter-user");
        request.setPassword("secret123");
        request.setRole("SHELTER");
        request.setFullName("Shelter Manager");
        request.setEmail("shelter@example.com");
        request.setPhone("1234567890");

        when(userRepository.existsByUsername("shelter-user")).thenReturn(false);
        when(encoder.encode("secret123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = controller.registerUser(request);

        assertEquals(200, response.getStatusCode().value());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(User.Role.ROLE_SHELTER, userCaptor.getValue().getRole());
    }
}
