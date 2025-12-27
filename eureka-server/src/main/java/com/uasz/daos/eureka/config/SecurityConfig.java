package com.uasz.daos.eureka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de sécurité pour Eureka Server
 * Active l'authentification Basic Auth pour protéger le dashboard et les endpoints
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Permettre l'accès au health check pour Docker health checks
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {}); // Active Basic Auth

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Créer un utilisateur admin pour Eureka
        // IMPORTANT: En production, utiliser des credentials depuis Vault
        UserDetails admin = User.builder()
            .username("${eureka.security.username:admin}")
            .password(passwordEncoder().encode("${eureka.security.password:admin}"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
