package com.soutenance.soutenance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client → service-planification (port 8083)
 * Vérifie la disponibilité des salles et créneaux avant planification.
 */
@FeignClient(name = "service-planification", url = "${services.planification.url}")
public interface PlanificationClient {

    @GetMapping("/api/creneaux/{id}/disponible")
    Boolean creneauDisponible(@PathVariable("id") String id);

    @GetMapping("/api/salles/{id}/disponible")
    Boolean salleDisponible(@PathVariable("id") String id);
}
