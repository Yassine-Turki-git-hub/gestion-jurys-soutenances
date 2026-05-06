package com.soutenance.soutenance.controller;

import com.soutenance.soutenance.dto.SoutenanceDTO;
import com.soutenance.soutenance.service.SoutenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soutenances")
@RequiredArgsConstructor
@Tag(name = "Soutenances", description = "Gestion des soutenances")
public class SoutenanceController {

    private final SoutenanceService soutenanceService;

    // ── GET ALL ───────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Lister toutes les soutenances")
    public ResponseEntity<List<SoutenanceDTO>> getAll() {
        return ResponseEntity.ok(soutenanceService.getAll());
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une soutenance par ID")
    public ResponseEntity<SoutenanceDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(soutenanceService.getById(id));
    }

    // ── GET BY ETUDIANT ───────────────────────────────────────────────────────

    @GetMapping("/etudiant/{etudiantId}")
    @Operation(summary = "Lister les soutenances d'un étudiant")
    public ResponseEntity<List<SoutenanceDTO>> getByEtudiant(@PathVariable String etudiantId) {
        return ResponseEntity.ok(soutenanceService.getByEtudiant(etudiantId));
    }

    // ── GET BY ENCADRANT ──────────────────────────────────────────────────────

    @GetMapping("/encadrant/{encadrantId}")
    @Operation(summary = "Lister les soutenances d'un encadrant")
    public ResponseEntity<List<SoutenanceDTO>> getByEncadrant(@PathVariable String encadrantId) {
        return ResponseEntity.ok(soutenanceService.getByEncadrant(encadrantId));
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Planifier une nouvelle soutenance")
    public ResponseEntity<SoutenanceDTO> creer(@Valid @RequestBody SoutenanceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(soutenanceService.creer(dto));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une soutenance")
    public ResponseEntity<SoutenanceDTO> modifier(
            @PathVariable String id,
            @Valid @RequestBody SoutenanceDTO dto) {
        return ResponseEntity.ok(soutenanceService.modifier(id, dto));
    }

    // ── STATUS TRANSITIONS ────────────────────────────────────────────────────

    @PatchMapping("/{id}/demarrer")
    @Operation(summary = "Démarrer une soutenance (PLANIFIEE → EN_COURS)")
    public ResponseEntity<SoutenanceDTO> demarrer(@PathVariable String id) {
        return ResponseEntity.ok(soutenanceService.demarrer(id));
    }

    @PatchMapping("/{id}/valider")
    @Operation(summary = "Valider une soutenance (→ TERMINEE)")
    public ResponseEntity<SoutenanceDTO> valider(@PathVariable String id) {
        return ResponseEntity.ok(soutenanceService.valider(id));
    }

    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler une soutenance (→ ANNULEE)")
    public ResponseEntity<SoutenanceDTO> annuler(@PathVariable String id) {
        return ResponseEntity.ok(soutenanceService.annuler(id));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une soutenance")
    public ResponseEntity<Void> supprimer(@PathVariable String id) {
        soutenanceService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/planifier")
    @Operation(summary = "Affecter une salle et un créneau à une soutenance (appelé par service-planification)")
    public ResponseEntity<SoutenanceDTO> planifier(
            @PathVariable String id,
            @RequestParam String salleId,
            @RequestParam String creneauId) {
        return ResponseEntity.ok(soutenanceService.planifier(id, salleId, creneauId));
    }

    @DeleteMapping("/{id}/planification")
    @Operation(summary = "Supprimer la planification (salle + créneau) d'une soutenance")
    public ResponseEntity<SoutenanceDTO> supprimerPlanification(@PathVariable String id) {
        return ResponseEntity.ok(soutenanceService.supprimerPlanification(id));
    }
}
