package tn.microservices.serviceplanification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.microservices.serviceplanification.service.PlanificationService;

@RestController
@RequestMapping("/api/planification")
@RequiredArgsConstructor
public class PlanificationController {

    private final PlanificationService planificationService;

    /**
     * MANUAL PLANNING - Manually assign a room and time slot to a presentation
     * Used when an administrator wants to control the scheduling manually
     *
     * @param soutenanceId The ID of the presentation to plan
     * @param salleId The ID of the room to assign
     * @param creneauId The ID of the time slot to assign
     * @return 200 OK if successful
     */
    @PostMapping("/manuel")
    public ResponseEntity<String> planifierManuel(@RequestParam Long soutenanceId,
                                                  @RequestParam Long salleId,
                                                  @RequestParam Long creneauId) {
        planificationService.planifierManuel(soutenanceId, salleId, creneauId);
        return new ResponseEntity<>("Planification manuelle réussie pour la soutenance " + soutenanceId,
                HttpStatus.OK);
    }

    /**
     * AUTOMATIC PLANNING - Automatically assign rooms and time slots to presentations
     * The system tries to avoid conflicts by checking encadrants and jury members availability
     *
     * @return 200 OK if successful
     */
    @PostMapping("/auto")
    public ResponseEntity<String> planifierAuto() {
        planificationService.planifierAuto();
        return new ResponseEntity<>("Planification automatique terminée avec succès", HttpStatus.OK);
    }

    /**
     * MODIFY PLANNING - Modify an existing presentation schedule
     * Allows changing the room and/or time slot of an already planned presentation
     * Verifies there are no conflicts before applying the change
     *
     * @param soutenanceId The ID of the presentation to modify
     * @param newSalleId The new room ID (optional, null to keep current)
     * @param newCreneauId The new time slot ID (optional, null to keep current)
     * @return 200 OK if successful
     */
    @PutMapping("/modifier")
    public ResponseEntity<String> modifierPlanification(@RequestParam Long soutenanceId,
                                                        @RequestParam(required = false) Long newSalleId,
                                                        @RequestParam(required = false) Long newCreneauId) {
        planificationService.modifierPlanification(soutenanceId, newSalleId, newCreneauId);
        return new ResponseEntity<>("Planification modifiée avec succès pour la soutenance " + soutenanceId,
                HttpStatus.OK);
    }
}
