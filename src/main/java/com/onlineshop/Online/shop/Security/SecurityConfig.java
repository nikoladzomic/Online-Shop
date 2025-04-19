package com.onlineshop.Online.shop.Security;

import com.onlineshop.Online.shop.Repository.UserRepository;
import com.onlineshop.Online.shop.Service.CustomOAuth2UserService;
import com.onlineshop.Online.shop.Service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import com.onlineshop.Online.shop.Model.UserDetailsImpl;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("http://localhost:5174"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Refresh-Token"));
        configuration.setExposedHeaders(List.of("New-Access-Token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .xssProtection(withDefaults())
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOAuth2UserService) // Promenjeno u oidcUserService
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oauth2User.getAttribute("email");

                            String jwt = jwtUtils.generateTokenFromUsername(email);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"token\":\"" + jwt + "\",\"username\":\"" + email + "\"}"
                            );
                        })
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui.html",
                                "/login/oauth2/**",
                                "/oauth2/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/products/**")
                        .permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER","ADMIN")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}