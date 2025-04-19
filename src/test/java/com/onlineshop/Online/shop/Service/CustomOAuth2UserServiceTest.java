package com.onlineshop.Online.shop.Service;

import com.onlineshop.Online.shop.Model.User;
import com.onlineshop.Online.shop.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CustomOAuth2UserService customOAuth2UserService;

    private OidcUserRequest userRequest;
    private Map<String, Object> attributes;
    private OidcUser oidcUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("sub", "123456789");
        attributes.put("name", "Nikola");

        // Create basic client registration
        ClientRegistration clientRegistration = ClientRegistration
                .withRegistrationId("google")
                .clientId("clientId")
                .clientSecret("clientSecret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .userNameAttributeName("sub")
                .scope("openid", "profile", "email")
                .build();

        // Create OAuth tokens
        OidcIdToken idToken = new OidcIdToken(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                attributes
        );

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "accessToken",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        userRequest = new OidcUserRequest(clientRegistration, accessToken, idToken);

        // Mock OidcUser that would normally be returned by super.loadUser
        oidcUser = mock(OidcUser.class);
        when(oidcUser.getAttributes()).thenReturn(attributes);

        // Create service and spy it to avoid calling super.loadUser
        customOAuth2UserService = spy(new CustomOAuth2UserService(userRepository, passwordEncoder));
        doReturn(oidcUser).when(customOAuth2UserService).super$loadUser(any());
    }

    @Test
    void loadUser_WithNewUser_ShouldCreateAndReturnOidcUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByEmailAndProviderNot("test@example.com", "GOOGLE")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // When
        OidcUser result = customOAuth2UserService.loadUser(userRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).save(argThat(user ->
                "test@example.com".equals(user.getEmail()) &&
                        "Nikola".equals(user.getUsername()) &&  // Changed from "Nikola Dzomic" to match the mock data
                        "GOOGLE".equals(user.getProvider()) &&
                        "123456789".equals(user.getProviderId()) &&  // Changed from "1234567890" to match the mock data
                        "USER".equals(user.getRole()) &&
                        user.isEnabled()
        ));
        assertEquals(oidcUser, result);  // Verify we return the same OidcUser
    }

    @Test
    void loadUser_WithExistingUser_ShouldReturnOidcUserWithoutSavingNewUser() {
        // Given
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");
        existingUser.setUsername("Nikola");
        existingUser.setProvider("GOOGLE");
        existingUser.setProviderId("123456789");
        existingUser.setRole("USER");
        existingUser.setEnabled(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // When
        OidcUser result = customOAuth2UserService.loadUser(userRequest);

        // Then
        assertNotNull(result);
        verify(userRepository, never()).save(any(User.class));
        assertEquals(oidcUser, result);
    }

    @Test
    void loadUser_WithEmailAlreadyExistsForDifferentProvider_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByEmailAndProviderNot("test@example.com", "GOOGLE")).thenReturn(true);

        // When & Then
        OAuth2AuthenticationException exception = assertThrows(OAuth2AuthenticationException.class,
                () -> customOAuth2UserService.loadUser(userRequest));

        assertEquals("email_already_exists", exception.getError().getErrorCode());
    }
}