package tn.microservices.serviceplanification.controller;

import tn.microservices.serviceplanification.service.PlanificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planification")
@RequiredArgsConstructor
public class PlanificationController {

    private final PlanificationService planificationService;

    @PostMapping("/manuel")
    public void manuel(@RequestParam Long soutenanceId,
                       @RequestParam Long salleId,
                       @RequestParam Long creneauId) {

        planificationService.planifierManuel(
                soutenanceId, salleId, creneauId
        );
    }

    @PostMapping("/auto")
    public void auto() {
        planificationService.planifierAuto();
    }
}
