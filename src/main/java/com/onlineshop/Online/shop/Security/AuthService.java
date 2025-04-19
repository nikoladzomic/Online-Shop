package com.onlineshop.Online.shop.Security;

import com.onlineshop.Online.shop.Model.User;
import com.onlineshop.Online.shop.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        String accessToken = jwtUtils.generateJwtToken(authentication);

        String refreshToken = jwtUtils.generateRefreshToken((UserDetails) authentication.getPrincipal());

        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        JwtResponse tokens = new JwtResponse(accessToken, refreshToken);
        return new LoginResponse(userDetails.getUsername(), roles, tokens);
    }

    public JwtResponse refreshToken(String refreshToken) {

        if (jwtUtils.validateJwtToken(refreshToken)) {
            String username = jwtUtils.getUserNameFromJwtToken(refreshToken);

            User user = userRepository.findByUsername(username).orElseThrow();
            if (refreshToken.equals(user.getRefreshToken())) {
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getAuthorities()
                );
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                String newAccessToken = jwtUtils.generateJwtToken(authentication);
                return new JwtResponse(newAccessToken, refreshToken);
            }
        }

        throw new RuntimeException("Invalid refresh token");
    }

    public void logout(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}