package tn.microservices.serviceplanification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import tn.microservices.serviceplanification.config.FeignConfig;
import tn.microservices.serviceplanification.dto.SoutenanceDTO;

import java.util.List;

@FeignClient(
        name = "api-gateway",
        url = "${services.gateway.url}",
        configuration = FeignConfig.class
)
public interface SoutenanceClient {

    @GetMapping("/api/soutenances")
    List<SoutenanceDTO> getAll();

    @PutMapping("/api/soutenances/{id}/planifier")
    void affecter(@PathVariable Long id,
                  @RequestParam Long salleId,
                  @RequestParam Long creneauId);
}
