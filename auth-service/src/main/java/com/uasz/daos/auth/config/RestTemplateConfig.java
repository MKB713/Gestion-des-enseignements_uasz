package com.uasz.daos.auth.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration pour RestTemplate avec Eureka et Load Balancing
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate configuré avec Load Balancing pour Eureka
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Configuration supplémentaire si nécessaire
        // restTemplate.setErrorHandler(new CustomErrorHandler());

        return restTemplate;
    }
}