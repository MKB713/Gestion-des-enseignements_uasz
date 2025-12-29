package com.uasz.daos.choix.controller;

import com.uasz.daos.choix.config.ApplicationConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RefreshScope
public class ConfigTestController {

    private final ApplicationConfigProperties configProperties;

    @Value("${app.message:Message par défaut}")
    private String appMessage;

    @Value("${app.environment:unknown}")
    private String appEnvironment;

    public ConfigTestController(ApplicationConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @GetMapping("/info")
    public Map<String, Object> getConfigInfo() {
        Map<String, Object> config = new HashMap<>();
        config.put("applicationName", configProperties.getApplicationName());
        config.put("serverPort", configProperties.getServerPort());
        config.put("showSql", configProperties.getShowSql());
        config.put("appMessage", appMessage);
        config.put("appEnvironment", appEnvironment);
        return config;
    }
}