package com.uasz.daos.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Configuration properties pour JWT
 * Utilise @RefreshScope pour permettre le rafraîchissement dynamique
 * des valeurs depuis Config Server sans redémarrage
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigProperties {

    private String secret;
    private Long expiration;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
}
