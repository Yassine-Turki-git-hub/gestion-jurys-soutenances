package com.soutenance.soutenance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client → service-utilisateurs (port 8081)
 * Utilisé pour vérifier qu'un étudiant et un encadrant existent
 * avant de planifier une soutenance.
 */
@FeignClient(name = "service-utilisateurs", url = "${services.utilisateurs.url}")
public interface UtilisateurClient {

    @GetMapping("/api/etudiants/{id}/exists")
    Boolean etudiantExists(@PathVariable("id") Long id);

    @GetMapping("/api/enseignants/{id}/exists")
    Boolean encadrantExists(@PathVariable("id") Long id);
}
