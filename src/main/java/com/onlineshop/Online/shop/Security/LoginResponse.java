package com.onlineshop.Online.shop.Security;

import java.util.List;

public class LoginResponse {
    private String username;
    private List<String> roles;
    private JwtResponse tokens;

    public LoginResponse(String username, List<String> roles, JwtResponse tokens) {
        this.username = username;
        this.roles = roles;
        this.tokens = tokens;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public JwtResponse getTokens() {
        return tokens;
    }

    public void setTokens(JwtResponse tokens) {
        this.tokens = tokens;
    }
}