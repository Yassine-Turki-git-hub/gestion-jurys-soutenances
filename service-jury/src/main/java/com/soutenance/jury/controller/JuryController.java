package com.soutenance.jury.controller;

import com.soutenance.jury.dto.JuryDTO;
import com.soutenance.jury.service.JuryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jury/membres")
@RequiredArgsConstructor
public class JuryController {

    private final JuryService juryService;

    @PostMapping
    public ResponseEntity<JuryDTO.Response> affecter(@RequestBody JuryDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(juryService.affecterMembre(request));
    }

    @GetMapping
    public ResponseEntity<List<JuryDTO.Response>> getAllMembres() {
        return ResponseEntity.ok(juryService.getAllMembres());
    }

    @GetMapping("/{soutenanceId}")
    public ResponseEntity<List<JuryDTO.Response>> getMembres(@PathVariable String soutenanceId) {
        return ResponseEntity.ok(juryService.getMembresParSoutenance(soutenanceId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> retirer(@PathVariable String id) {
        juryService.retirerMembre(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/auto/{soutenanceId}")
    public ResponseEntity<List<JuryDTO.Response>> autoAffecter(
            @PathVariable String soutenanceId,
            @RequestParam String encadrantId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(juryService.autoAffecterJury(soutenanceId, encadrantId));
    }
}
