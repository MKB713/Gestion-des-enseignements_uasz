package com.uasz.daos.choix.client;

import com.uasz.daos.choix.dto.MaquetteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign Client pour communiquer avec le microservice Maquette
 * Le nom "maquette-service" correspond au spring.application.name du microservice Maquette
 */
@FeignClient(name = "maquette-service")
public interface MaquetteClient {

    /**
     * Récupérer une maquette par son ID
     * @param id L'identifiant de la maquette
     * @return La maquette trouvée
     */
    @GetMapping("/api/maquettes/{id}")
    MaquetteDTO getMaquetteById(@PathVariable("id") Long id);

    /**
     * Récupérer toutes les maquettes
     * @return Liste de toutes les maquettes
     */
    @GetMapping("/api/maquettes")
    List<MaquetteDTO> getAllMaquettes();

    /**
     * Vérifier si une maquette existe
     * @param id L'identifiant de la maquette
     * @return true si la maquette existe, false sinon
     */
    @GetMapping("/api/maquettes/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}