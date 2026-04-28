package com.soutenance.service_utilisateurs.controller;

import com.soutenance.service_utilisateurs.entity.Enseignant;
import com.soutenance.service_utilisateurs.repository.EnseignantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enseignants")
public class EnseignantController {

    @Autowired
    private EnseignantRepository enseignantRepository;

    // Lire tous les enseignants
    @GetMapping
    public List<Enseignant> getAllEnseignants() {
        return enseignantRepository.findAll();
    }

    // Ajouter un nouvel enseignant
    @PostMapping
    public Enseignant saveEnseignant(@RequestBody Enseignant enseignant) {
        return enseignantRepository.save(enseignant);
    }
}