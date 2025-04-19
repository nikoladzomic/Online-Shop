package com.onlineshop.Online.shop.Service;

import com.onlineshop.Online.shop.Model.User;
import com.onlineshop.Online.shop.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // This method can be overridden/mocked in tests
    OidcUser super$loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        return super.loadUser(userRequest);
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super$loadUser(userRequest);
        return processOidcUser(userRequest, oidcUser);
    }

    protected OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        Map<String, Object> attributes = oidcUser.getAttributes();

        logger.info("Atributi korisnika: {}", attributes);
        String email = (String) attributes.get("email");
        String providerId = (String) attributes.get("sub");
        String name = (String) attributes.get("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            if (userRepository.existsByEmailAndProviderNot(email, "GOOGLE")) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("email_already_exists",
                                "Email je već registrovan sa drugim providerom",
                                null));
            }

            logger.info("Kreiranje novog korisnika za email: {}", email);
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name != null ? name : email.split("@")[0]);
            newUser.setProvider("GOOGLE");
            newUser.setProviderId(providerId);
            newUser.setRole("USER");
            newUser.setEnabled(true);
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

            User savedUser = userRepository.save(newUser);
            logger.info("Korisnik sačuvan sa ID: {}", savedUser.getId());
            return savedUser;
        });

        logger.info("Korisnik ID: {}", user.getId());
        return oidcUser;
    }
}