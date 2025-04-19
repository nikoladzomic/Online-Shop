package com.onlineshop.Online.shop.Security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Operation(summary = "Admin dashboard", description = "Access the admin dashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully accessed"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome to the admin dashboard!";
    }

    @Operation(summary = "List all users", description = "Get a list of all users (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/users")
    public String listUsers() {
        return "List of all users (only accessible by admins)";
    }
}