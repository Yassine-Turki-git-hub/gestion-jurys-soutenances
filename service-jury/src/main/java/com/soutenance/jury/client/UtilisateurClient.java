package com.soutenance.jury.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-utilisateurs", url = "${utilisateurs.service.url}")
public interface UtilisateurClient {

    @GetMapping("/api/enseignants/{id}")
    EnseignantClientDTO getEnseignantById(@PathVariable("id") String id);
}
