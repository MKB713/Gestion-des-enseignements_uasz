package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.model.Formation;
import com.uasz.daos.maquette.service.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquette/formations")
// @CrossOrigin(origins = "*")  <--- RETIRÉ pour éviter le conflit avec la Gateway
public class FormationController {

    @Autowired
    private FormationService formationService;

    @GetMapping
    public ResponseEntity<List<Formation>> listerFormations() {
        return new ResponseEntity<>(formationService.getAllFormations(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Formation> ajouterFormation(@RequestBody Formation formation) {
        return new ResponseEntity<>(formationService.createFormation(formation), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Formation> modifierFormation(@PathVariable Long id, @RequestBody Formation formation) {
        return new ResponseEntity<>(formationService.updateFormation(id, formation), HttpStatus.OK);
    }

    @PatchMapping("/{id}/archiver")
    public ResponseEntity<Formation> archiverFormation(@PathVariable Long id) {
        return new ResponseEntity<>(formationService.archiveFormation(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Formation> activerFormation(@PathVariable Long id) {
        return new ResponseEntity<>(formationService.activerFormation(id), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Formation> detailsFormation(@PathVariable Long id) {
        return new ResponseEntity<>(formationService.getFormationById(id), HttpStatus.OK);
    }
}