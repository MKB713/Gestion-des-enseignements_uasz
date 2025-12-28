package com.uasz.daos.choix.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI choixEnseignementServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Choix Enseignement Service API")
                        .description("API de gestion des choix d'enseignements des enseignants pour DAOS (UASZ)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe DAOS")
                                .email("daos@uasz.edu.sn")
                                .url("https://uasz.edu.sn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8084")
                                .description("Serveur de développement (Direct)"),
                        new Server()
                                .url("http://localhost:8080/api/choix-enseignements")
                                .description("Serveur de développement (via API Gateway)")));
    }
}
