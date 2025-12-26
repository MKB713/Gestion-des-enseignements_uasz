package com.uasz.daos.emploitemps.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuration du cache Redis pour améliorer les performances
 * Utilisé notamment pour :
 * - Cache des disponibilités de salles
 * - Cache des emplois du temps
 * - Cache des séances fréquemment consultées
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure le gestionnaire de cache Redis
     *
     * @param connectionFactory Factory de connexion Redis
     * @return CacheManager configuré
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configuration par défaut du cache
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // TTL par défaut : 30 minutes
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Configuration spécifique pour les disponibilités de salles (cache court)
        RedisCacheConfiguration sallesDispoConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // 10 minutes pour les disponibilités
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()));

        // Configuration spécifique pour les emplois du temps (cache long)
        RedisCacheConfiguration emploiDuTempsConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2)) // 2 heures pour les emplois du temps
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withCacheConfiguration("sallesDisponibles", sallesDispoConfig)
                .withCacheConfiguration("sallesDisponiblesCapacite", sallesDispoConfig)
                .withCacheConfiguration("emploiDuTempsHebdo", emploiDuTempsConfig)
                .withCacheConfiguration("emploiDuTempsSemestre", emploiDuTempsConfig)
                .build();
    }

    /**
     * ALTERNATIVE : Configuration sans Redis (cache en mémoire)
     * À utiliser si Redis n'est pas disponible
     * Décommenter cette méthode et commenter celle au-dessus
     */
    /*
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("sallesDisponibles"),
            new ConcurrentMapCache("sallesDisponiblesCapacite"),
            new ConcurrentMapCache("emploiDuTempsHebdo"),
            new ConcurrentMapCache("emploiDuTempsSemestre")
        ));
        return cacheManager;
    }
    */
}