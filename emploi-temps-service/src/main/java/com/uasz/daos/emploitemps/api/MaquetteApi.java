package com.uasz.daos.emploitemps.api;

import com.uasz.daos.emploitemps.dto.generation.ClasseDTO; // Use generation DTO
import com.uasz.daos.emploitemps.dto.generation.ECDTO; // Use generation DTO
import com.uasz.daos.emploitemps.dto.generation.RepartitionDTO; // Use generation DTO
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "maquette-service")
public interface MaquetteApi {

    @GetMapping("/api/ecs/{id}")
    ECDTO getECById(@PathVariable("id") Long id);

    @GetMapping("/api/ecs")
    List<ECDTO> getAllEcs();

    @GetMapping("/api/classes")
    List<ClasseDTO> getClassesByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("/api/repartitions")
    List<RepartitionDTO> getRepartitionsByClasseIds(@RequestParam("classeIds") List<Long> classeIds);
}
