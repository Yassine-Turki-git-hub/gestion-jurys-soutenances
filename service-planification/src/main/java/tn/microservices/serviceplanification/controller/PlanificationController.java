package tn.microservices.serviceplanification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.microservices.serviceplanification.service.PlanificationService;

import java.util.List;

@RestController
@RequestMapping("/api/planification")
@RequiredArgsConstructor
public class PlanificationController {

    private final PlanificationService planificationService;

    /**
     * MANUAL PLANNING
     */
    @PostMapping("/manuel")
    public ResponseEntity<String> planifierManuel(@RequestParam Long soutenanceId,
                                                  @RequestParam Long salleId,
                                                  @RequestParam Long creneauId) {
        planificationService.planifierManuel(soutenanceId, salleId, creneauId);
        return new ResponseEntity<>(
                "Planification manuelle réussie pour la soutenance " + soutenanceId,
                HttpStatus.OK
        );
    }

    /**
     * AUTOMATIC PLANNING
     */
    @PostMapping("/auto")
    public ResponseEntity<String> planifierAuto() {
        planificationService.planifierAuto();
        return new ResponseEntity<>(
                "Planification automatique terminée avec succès",
                HttpStatus.OK
        );
    }

    /**
     * MODIFY PLANNING
     */
    @PutMapping("/modifier")
    public ResponseEntity<String> modifierPlanification(@RequestParam Long soutenanceId,
                                                        @RequestParam(required = false) Long newSalleId,
                                                        @RequestParam(required = false) Long newCreneauId) {
        planificationService.modifierPlanification(soutenanceId, newSalleId, newCreneauId);
        return new ResponseEntity<>(
                "Planification modifiée avec succès pour la soutenance " + soutenanceId,
                HttpStatus.OK
        );
    }

    /**
     * DELETE PLANNING (NEW)
     */
    @DeleteMapping("/{soutenanceId}")
    public ResponseEntity<Void> supprimerPlanification(@PathVariable Long soutenanceId) {
        planificationService.supprimerPlanification(soutenanceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * BATCH PLANNING (NEW)
     */
    @PostMapping("/lot")
    public ResponseEntity<PlanificationService.BatchPlanificationResult> planifierLot(
            @RequestBody List<Long> soutenanceIds) {

        PlanificationService.BatchPlanificationResult result =
                planificationService.planifierLot(soutenanceIds);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}