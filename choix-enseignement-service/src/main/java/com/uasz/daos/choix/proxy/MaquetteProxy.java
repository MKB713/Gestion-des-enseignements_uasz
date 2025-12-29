package com.uasz.daos.choix.proxy;

import com.uasz.daos.choix.dtos.UEDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name = Nom du service dans Eureka
@FeignClient(name = "maquette-service")
public interface MaquetteProxy {

    // Adaptez l'URL selon votre MaquetteController (/maquettes/ues/{id} ou /ues/{id})
    // Supposons que votre contrôleur dans maquette-service répond sur /maquettes/ues/{id}
    @GetMapping("/maquettes/ues/{id}")
    UEDTO getUEById(@PathVariable("id") Long id);
}