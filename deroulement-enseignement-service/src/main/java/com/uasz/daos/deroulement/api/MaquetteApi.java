package com.uasz.daos.deroulement.api;

import com.uasz.daos.deroulement.dto.ECDTO;
import com.uasz.daos.deroulement.dto.FiliereDTO;
import com.uasz.daos.deroulement.dto.NiveauDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "maquette-service")
public interface MaquetteApi {

    @GetMapping("/api/filieres/{id}")
    FiliereDTO getFiliereById(@PathVariable("id") Long id);

    @GetMapping("/api/niveaux/{id}")
    NiveauDTO getNiveauById(@PathVariable("id") Long id);
    @GetMapping("/api/ecs/{id}")
    ECDTO getECById(@PathVariable("id") Long id);
}
