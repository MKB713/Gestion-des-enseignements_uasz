package com.uasz.daos.choix.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Feign pour activer les logs et gérer les erreurs
 */
@Configuration
public class FeignConfig {

    /**
     * Active les logs Feign pour le debug
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}