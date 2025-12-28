package com.uasz.daos.emploitemps.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;

/**
 * Configuration pour l'exécution asynchrone
 * Permet la génération asynchrone des PDF volumineux
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Configurer un pool de threads pour les tâches asynchrones
     * Utilisé notamment pour la génération de PDF
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-pdf-");
        executor.initialize();
        return executor;
    }
}