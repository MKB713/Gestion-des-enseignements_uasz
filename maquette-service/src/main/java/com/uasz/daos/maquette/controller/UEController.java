package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.model.UE;
import com.uasz.daos.maquette.service.UEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquette/ues")
public class UEController {

    @Autowired
    private UEService ueService;

    @GetMapping
    public ResponseEntity<List<UE>> getAllUEs() {
        return ResponseEntity.ok(ueService.getAllUEs());
    }

    @GetMapping("/archives")
    public ResponseEntity<List<UE>> getArchivedUEs() {
        return ResponseEntity.ok(ueService.getArchivedUEs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UE> getUEById(@PathVariable Long id) {
        return ResponseEntity.ok(ueService.getUEById(id));
    }

    @PostMapping
    public ResponseEntity<UE> createUE(@RequestBody UE ue) {
        ueService.saveUE(ue); // Assurez-vous que votre service retourne l'objet si possible, sinon renvoyez
                              // OK
        return new ResponseEntity<>(ue, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UE> updateUE(@PathVariable Long id, @RequestBody UE ue) {
        ue.setId(id);
        ueService.saveUE(ue);
        return ResponseEntity.ok(ue);
    }

    @PatchMapping("/{id}/archiver")
    public ResponseEntity<Void> archiver(@PathVariable Long id) {
        ueService.archiver(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/restaurer")
    public ResponseEntity<Void> restaurer(@PathVariable Long id) {
        ueService.restaurer(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Void> activer(@PathVariable Long id) {
        ueService.activer(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiver(@PathVariable Long id) {
        ueService.desactiver(id);
        return ResponseEntity.ok().build();
    }
}