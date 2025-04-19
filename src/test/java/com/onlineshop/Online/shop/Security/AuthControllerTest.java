package com.onlineshop.Online.shop.Security;

import com.onlineshop.Online.shop.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private RefreshTokenRequest refreshTokenRequest;
    private JwtResponse jwtResponse;
    private LogoutRequest logoutRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpassword");

        JwtResponse tokens = new JwtResponse("test-access-token", "test-refresh-token");

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        loginResponse = new LoginResponse("testuser", roles, tokens);

        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("test-refresh-token");

        // Create JwtResponse with proper constructor
        jwtResponse = new JwtResponse("new-access-token", "test-refresh-token");

        logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("testuser");

        ReflectionTestUtils.setField(authController, "authService", authService);
    }

    @Test
    void login_Success() {
        // Arrange
        LoginResponse mockedResponse = loginResponse; // Keep a reference
        System.out.println("Setting up mock to return: " + mockedResponse);

        // First check what happens when we call the mock directly
        when(authService.login(any(LoginRequest.class))).thenReturn(mockedResponse);

        try {
            LoginResponse testResponse = authService.login(loginRequest);
            System.out.println("Mock direct call returned: " + testResponse);
        } catch (Exception e) {
            System.out.println("Mock direct call threw exception: " + e);
            e.printStackTrace();
        }

        // Now try the controller call
        try {
            // Act
            System.out.println("Calling controller login");
            ResponseEntity<?> response = authController.login(loginRequest);
            System.out.println("Controller returned: " + response.getStatusCode() + " body: " + response.getBody());

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockedResponse, response.getBody());
        } catch (Exception e) {
            System.out.println("Controller call threw exception: " + e);
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void refreshToken_ShouldReturnNewToken() {

        when(authService.refreshToken(anyString())).thenReturn(jwtResponse);

        //when
        ResponseEntity<?> response = authController.refreshToken(refreshTokenRequest);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof JwtResponse);

        verify(authService, times(1)).refreshToken(anyString());
    }

    @Test
    void logout_Success() {

        doNothing().when(authService).logout(anyString());

        //when
        ResponseEntity<?> response = authController.logout(logoutRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
        verify(authService, times(1)).logout(anyString());
    }
}