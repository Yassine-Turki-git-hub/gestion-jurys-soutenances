package com.soutenance.service_utilisateurs.controller;

import com.soutenance.service_utilisateurs.entity.Etudiant;
import com.soutenance.service_utilisateurs.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.soutenance.service_utilisateurs.dto.LoginRequest;
import com.soutenance.service_utilisateurs.dto.RegisterRequest;
import java.util.List;
@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    @Autowired
    private EtudiantRepository etudiantRepository;

    @GetMapping
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) { // Modifié en Long
        return etudiantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Etudiant createEtudiant(@RequestBody Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Etudiant etudiant) {
        // L'ID est auto-incrémenté, on ne vérifie plus existsById
        if(etudiantRepository.findByEmail(etudiant.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email étudiant déjà existant");
        }
        return ResponseEntity.ok(etudiantRepository.save(etudiant));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Etudiant etudiant) {
        return etudiantRepository.findByEmail(etudiant.getEmail())
            .map(e -> {
                if (e.getPassword().equals(etudiant.getPassword())) {
                    return ResponseEntity.ok(e);
                } else {
                    return ResponseEntity.status(401).body("Mot de passe incorrect");
                }
            })
            .orElse(ResponseEntity.status(404).body("Étudiant non trouvé"));
    }
}