package com.soutenance.service_utilisateurs.controller;

import com.soutenance.service_utilisateurs.entity.Enseignant;
import com.soutenance.service_utilisateurs.repository.EnseignantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; 
import com.soutenance.service_utilisateurs.dto.LoginRequest;
import com.soutenance.service_utilisateurs.dto.RegisterRequest;
import java.util.List;
@RestController
@RequestMapping("/api/enseignants")
public class EnseignantController {

    @Autowired
    private EnseignantRepository enseignantRepository;

    @GetMapping
    public List<Enseignant> getAllEnseignants() {
        return enseignantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enseignant> getEnseignantById(@PathVariable Long id) { // Modifié en Long
        return enseignantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Enseignant saveEnseignant(@RequestBody Enseignant enseignant) {
        return enseignantRepository.save(enseignant);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Enseignant enseignant) {
        // Optionnel : Vérifier si l'email existe déjà au lieu de l'ID
        if(enseignantRepository.findByEmail(enseignant.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }
        return ResponseEntity.ok(enseignantRepository.save(enseignant));
    }

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Enseignant request) {
    // 1. Chercher l'enseignant par son email
    return enseignantRepository.findByEmail(request.getEmail())
        .map(enseignant -> {
            // 2. Vérifier si le mot de passe correspond
            if (enseignant.getPassword().equals(request.getPassword())) {
                return ResponseEntity.ok("Connexion réussie pour " + enseignant.getNom());
            } else {
                return ResponseEntity.status(401).body("Mot de passe incorrect");
            }
        })
        // 3. Si l'email n'existe pas
        .orElse(ResponseEntity.status(401).body("Identifiants incorrects"));
}
}