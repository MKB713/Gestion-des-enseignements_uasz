package com.uasz.daos.choix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing
public class ChoixEnseignementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChoixEnseignementServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("Choix Enseignement Service démarré !");
        System.out.println("Port: 8084");
        System.out.println("========================================");
    }
}