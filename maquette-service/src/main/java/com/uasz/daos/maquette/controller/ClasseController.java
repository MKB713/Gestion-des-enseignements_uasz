package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.model.Classe;
import com.uasz.daos.maquette.service.ClasseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquette/classes")
@CrossOrigin(origins = "*")
public class ClasseController {
    @Autowired
    private ClasseService classeService;

    @GetMapping
    public ResponseEntity<List<Classe>> listerClasses() {
        return new ResponseEntity<>(classeService.listerTout(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Classe> ajouterClasse(@RequestBody Classe classe) {
        return new ResponseEntity<>(classeService.ajouterClasse(classe), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classe> modifierClasse(@PathVariable Long id, @RequestBody Classe classe) {
        return new ResponseEntity<>(classeService.modifierClasse(id, classe), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerClasse(@PathVariable Long id) {
        classeService.supprimerClasse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classe> detailsClasse(@PathVariable Long id) {
        Classe classe = classeService.detailsClasse(id);
        if (classe != null) {
            return new ResponseEntity<>(classe, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
