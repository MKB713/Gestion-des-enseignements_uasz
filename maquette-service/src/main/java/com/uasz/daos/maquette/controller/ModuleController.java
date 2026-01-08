package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.model.Module;
import com.uasz.daos.maquette.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquette/modules")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping
    public ResponseEntity<List<Module>> getAllModules() {
        return ResponseEntity.ok(moduleService.getAllModules());
    }

    @GetMapping("/archives")
    public ResponseEntity<List<Module>> getArchivedModules() {
        return ResponseEntity.ok(moduleService.getArchivedModules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Module> getModuleById(@PathVariable Long id) {
        Module module = moduleService.getModuleById(id);
        return module != null ? ResponseEntity.ok(module) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Module> createModule(@RequestBody Module module) {
        Module saved = moduleService.addModule(module);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Module> updateModule(@PathVariable Long id, @RequestBody Module module) {
        Module updated = moduleService.updateModule(id, module);
        return ResponseEntity.ok(updated);
    }

    // Actions spécifiques (Archiver / Restaurer)

    @PatchMapping("/{id}/archiver")
    public ResponseEntity<Void> archiveModule(@PathVariable Long id) {
        moduleService.archiveModule(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/restaurer")
    public ResponseEntity<Void> unarchiveModule(@PathVariable Long id) {
        moduleService.unarchiveModule(id);
        return ResponseEntity.ok().build();
    }
}