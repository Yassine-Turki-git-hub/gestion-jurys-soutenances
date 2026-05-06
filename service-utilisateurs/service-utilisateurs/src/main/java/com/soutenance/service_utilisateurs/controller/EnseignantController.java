package com.soutenance.service_utilisateurs.controller;

import com.soutenance.service_utilisateurs.dto.LoginResponse;
import com.soutenance.service_utilisateurs.entity.Enseignant;
import com.soutenance.service_utilisateurs.repository.EnseignantRepository;
import com.soutenance.service_utilisateurs.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enseignants")
public class EnseignantController {

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Enseignant> getAllEnseignants() {
        return enseignantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enseignant> getEnseignantById(@PathVariable Long id) {
        return enseignantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        return ResponseEntity.ok(enseignantRepository.existsById(id));
    }

    @PostMapping
    public Enseignant saveEnseignant(@RequestBody Enseignant enseignant) {
        return enseignantRepository.save(enseignant);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Enseignant enseignant) {
        if (enseignantRepository.findByEmail(enseignant.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }
        Enseignant saved = enseignantRepository.save(enseignant);
        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), "ENSEIGNANT");
        return ResponseEntity.ok(new LoginResponse(token, saved.getId(), saved.getNom(), saved.getPrenom(), saved.getEmail(), "ENSEIGNANT"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Enseignant request) {
        return enseignantRepository.findByEmail(request.getEmail())
                .map(enseignant -> {
                    if (enseignant.getPassword().equals(request.getPassword())) {
                        String role = Boolean.TRUE.equals(enseignant.getIsAdmin()) ? "ADMIN" : "ENSEIGNANT";
                        String token = jwtUtil.generateToken(enseignant.getId(), enseignant.getEmail(), role);
                        return ResponseEntity.ok((Object) new LoginResponse(token, enseignant.getId(), enseignant.getNom(), enseignant.getPrenom(), enseignant.getEmail(), role));
                    } else {
                        return ResponseEntity.status(401).body((Object) "Mot de passe incorrect");
                    }
                })
                .orElse(ResponseEntity.status(401).body("Identifiants incorrects"));
    }
}
