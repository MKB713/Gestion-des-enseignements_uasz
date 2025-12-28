package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.model.Semestre;
import com.uasz.daos.maquette.service.SemestreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquette/semestres")
@CrossOrigin(origins = "*")
public class SemestreController {

    @Autowired
    private SemestreService semestreService;

    @GetMapping
    public ResponseEntity<List<Semestre>> listerSemestres() {
        return new ResponseEntity<>(semestreService.listerTout(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Semestre> ajouterSemestre(@RequestBody Semestre semestre) {
        return new ResponseEntity<>(semestreService.ajouterSemestre(semestre), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Semestre> modifierSemestre(@PathVariable Long id, @RequestBody Semestre semestre) {
        return new ResponseEntity<>(semestreService.modifierSemestre(id, semestre), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSemestre(@PathVariable Long id) {
        semestreService.supprimerSemestre(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Semestre> detailsSemestre(@PathVariable Long id) {
        Semestre semestre = semestreService.detailsSemestre(id);
        return semestre != null ? new ResponseEntity<>(semestre, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
