package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Emploi;
import com.uasz.daos.emploitemps.service.EmploiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emplois")
public class EmploiController {

    @Autowired
    private EmploiService emploiService;

    @GetMapping
    public List<Emploi> listerEmplois() {
        return emploiService.getAllEmplois();
    }

    @GetMapping("/{id}")
    public Emploi chercherEmploi(@PathVariable Long id) {
        return emploiService.getEmploiById(id);
    }

    @PostMapping
    public Emploi creerEmploi(@RequestBody Emploi emploi) {
        return emploiService.createEmploi(emploi);
    }

    @PutMapping("/{id}")
    public Emploi modifierEmploi(@PathVariable Long id, @RequestBody Emploi emploi) {
        return emploiService.updateEmploi(id, emploi);
    }

    @DeleteMapping("/{id}")
    public void supprimerEmploi(@PathVariable Long id) {
        emploiService.deleteEmploi(id);
    }
}
