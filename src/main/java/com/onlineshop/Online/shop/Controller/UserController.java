package com.onlineshop.Online.shop.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.onlineshop.Online.shop.Model.User;
import com.onlineshop.Online.shop.Service.UserService;
import com.onlineshop.Online.shop.Service.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceInterface userService;

    @Autowired
    private UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user", description = "Creates a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.saveUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Get user by ID", description = "Returns a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
