package com.uasz.daos.deroulement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()  // Permet l'accès à toutes les pages sans authentification
            )
            .csrf(csrf -> csrf.disable())  // Désactive CSRF pour faciliter les tests
            .formLogin(form -> form.disable());  // Désactive le formulaire de login

        return http.build();
    }
}
