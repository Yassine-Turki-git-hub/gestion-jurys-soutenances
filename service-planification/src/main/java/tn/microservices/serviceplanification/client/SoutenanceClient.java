package tn.microservices.serviceplanification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import tn.microservices.serviceplanification.dto.SoutenanceDTO;

import java.util.List;

@FeignClient(
        name = "service-soutenance",
        url = "${services.soutenance.url}"
)
public interface SoutenanceClient {

    @GetMapping("/api/soutenances")
    List<SoutenanceDTO> getAll();

    @PutMapping("/api/soutenances/{id}/planifier")
    void affecter(@PathVariable String id,
                  @RequestParam String salleId,
                  @RequestParam String creneauId);

    @DeleteMapping("/api/soutenances/{id}/planification")
    void supprimerPlanification(@PathVariable String id);
}
