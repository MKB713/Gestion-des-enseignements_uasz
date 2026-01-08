package com.uasz.daos.choix.client;

import com.uasz.daos.choix.dtos.EnseignantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign Client pour communiquer avec le microservice Enseignant
 * Le nom "enseignant-service" doit correspondre au spring.application.name du microservice Enseignant
 */
@FeignClient(name = "enseignant-service")
public interface EnseignantClient {

    /**
     * Récupérer un enseignant par son ID
     * @param id L'identifiant de l'enseignant
     * @return L'enseignant trouvé
     */
    @GetMapping("/api/enseignants/{id}")
    EnseignantDTO getEnseignantById(@PathVariable("id") Long id);

    /**
     * Récupérer tous les enseignants actifs
     * @return Liste de tous les enseignants
     */
    @GetMapping("/api/enseignants")
    List<EnseignantDTO> getAllEnseignants();

    /**
     * Vérifier si un enseignant existe
     * @param id L'identifiant de l'enseignant
     * @return true si l'enseignant existe, false sinon
     */
    @GetMapping("/api/enseignants/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}