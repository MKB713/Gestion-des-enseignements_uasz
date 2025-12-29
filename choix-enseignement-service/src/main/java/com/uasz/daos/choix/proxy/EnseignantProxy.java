package com.uasz.daos.choix.proxy;

import com.uasz.daos.choix.dtos.EnseignantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name = Le nom EXACT du service cible tel qu'il apparaît dans Eureka
@FeignClient(name = "enseignant-service")
public interface EnseignantProxy {

    /**
     * Appelle GET http://enseignant-service/enseignants/{id}
     * Note: L'URL correspond au @RequestMapping du EnseignantController
     */
    @GetMapping("/enseignants/{id}")
    EnseignantDTO getEnseignantById(@PathVariable("id") Long id);
}