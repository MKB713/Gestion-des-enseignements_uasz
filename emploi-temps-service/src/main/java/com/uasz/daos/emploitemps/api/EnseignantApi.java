package com.uasz.daos.emploitemps.api;

import com.uasz.daos.emploitemps.dto.generation.EnseignantDTO; // Use generation DTO
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "enseignant-service")
public interface EnseignantApi {

    @GetMapping("/api/enseignants/{id}")
    EnseignantDTO getEnseignantById(@PathVariable("id") Long id);

    @GetMapping("/api/enseignants")
    List<EnseignantDTO> getAllEnseignants();
}
