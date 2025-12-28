package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.model.EC;
import com.uasz.daos.maquette.service.ECService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquette/ecs")
@CrossOrigin(origins = "*")
public class ECController {

    @Autowired
    private ECService ecService;

    @GetMapping
    public ResponseEntity<List<EC>> getAllECs() {
        return ResponseEntity.ok(ecService.getAllECs());
    }

    @GetMapping("/archives")
    public ResponseEntity<List<EC>> getArchivedECs() {
        return ResponseEntity.ok(ecService.getArchivedECs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EC> getECById(@PathVariable Long id) {
        EC ec = ecService.getECById(id);
        return ec != null ? ResponseEntity.ok(ec) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EC> createEC(@RequestBody EC ec) {
        EC saved = ecService.addEC(ec);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EC> updateEC(@PathVariable Long id, @RequestBody EC ec) {
        // Le service gère l'update via id
        EC updated = ecService.updateEC(id, ec);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/archiver")
    public ResponseEntity<Void> archiveEC(@PathVariable Long id) {
        ecService.archiveEC(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/restaurer")
    public ResponseEntity<Void> unarchiveEC(@PathVariable Long id) {
        ecService.unarchiveEC(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Void> activateEC(@PathVariable Long id) {
        ecService.activateEC(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> deactivateEC(@PathVariable Long id) {
        ecService.deactivateEC(id);
        return ResponseEntity.ok().build();
    }
}