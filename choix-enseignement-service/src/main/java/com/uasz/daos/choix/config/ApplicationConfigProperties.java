package com.uasz.daos.choix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Configuration properties de l'application
 * Utilise @RefreshScope pour permettre le rafraîchissement dynamique
 * des valeurs depuis Config Server sans redémarrage
 */
@Component
@RefreshScope
public class ApplicationConfigProperties {

    @Value("${spring.application.name:choix-enseignement-service}")
    private String applicationName;

    @Value("${server.port:8084}")
    private Integer serverPort;

    @Value("${spring.jpa.show-sql:false}")
    private Boolean showSql;

    public String getApplicationName() {
        return applicationName;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public Boolean getShowSql() {
        return showSql;
    }
}
