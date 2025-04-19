package com.onlineshop.Online.shop.Security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.aspectj.util.Reflection;
import org.junit.jupiter.api.BeforeEach;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtUtils jwtUtils;

    private final String testUsername = "testUser";
    private final String testJwtSecret = "secureJwtSecretKeyForTestingPurposesOnlyDoNotUseInProductionasdasdasdasdasdasdasdasd";
    private final int testJwtExpirationMs = 60000;
    private final int testRefreshTokenExpirationMs = 600000;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testJwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testJwtExpirationMs);
        ReflectionTestUtils.setField(jwtUtils, "refreshTokenExpirationMs", testRefreshTokenExpirationMs);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(testUsername);
    }

    @Test
    void generateJwtToken_ShouldReturnValidToken() {
        //given
        String token = jwtUtils.generateJwtToken(authentication);

        //then
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals(testUsername, jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void generateRefreshToken_ShouldGenerateValidRefreshToken() {
        //given
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        //then
        assertNotNull(refreshToken);
        assertTrue(jwtUtils.validateRefreshToken(refreshToken));
        assertEquals(testUsername, jwtUtils.getUserNameFromJwtToken(refreshToken));
    }

    @Test
    void getUserNameFromJwtToken_ShouldReturnUsername() {
        //given
        String token = jwtUtils.generateJwtToken(authentication);

        //when
        String username = jwtUtils.getUserNameFromJwtToken(token);

        //then
        assertEquals(testUsername, username);

    }


    @Test
    void validateJwtToken_ShouldReturnValidToken() {
        //given
        String token = jwtUtils.generateJwtToken(authentication);

        //then
        assertTrue(jwtUtils.validateJwtToken(token));

    }

    @Test
    void validateJwtToken_WithExpiredTime_ShouldReturnInvalidToken() {
        //given
        String expiredToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 120000))
                .setExpiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(Keys.hmacShaKeyFor(testJwtSecret.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS512)
                .compact();

        // When & Then
        assertFalse(jwtUtils.validateJwtToken(expiredToken));

    }

    @Test
    void validateRefreshToken_ShouldReturnValidRefreshToken() {
        //given
        String token = jwtUtils.generateRefreshToken(userDetails);

        //then
        assertTrue(jwtUtils.validateRefreshToken(token));
    }

    @Test
    void generateTokenFromUsername_ShouldReturnToken() {
        //given
        String token = jwtUtils.generateTokenFromUsername(testUsername);

        //then
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals(testUsername, jwtUtils.getUserNameFromJwtToken(token));

    }
}