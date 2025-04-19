package com.onlineshop.Online.shop.Security;

import com.onlineshop.Online.shop.Model.User;
import com.onlineshop.Online.shop.Repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public AuthController(JwtUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @Operation(summary = "User login", description = "Authenticate user and return tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @Operation(summary = "Refresh token", description = "Generate a new access token using a refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New access token generated"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            JwtResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @Operation(summary = "User logout", description = "Invalidate the refresh token and log out the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out"),
            @ApiResponse(responseCode = "400", description = "Logout failed")
    })

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        try {
            authService.logout(request.getUsername());
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout failed");
        }
    }
}