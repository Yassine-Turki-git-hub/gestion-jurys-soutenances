package com.soutenance.service_utilisateurs.controller;

import com.soutenance.service_utilisateurs.dto.LoginResponse;
import com.soutenance.service_utilisateurs.entity.Etudiant;
import com.soutenance.service_utilisateurs.repository.EtudiantRepository;
import com.soutenance.service_utilisateurs.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) {
        return etudiantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantRepository.existsById(id));
    }

    @PostMapping
    public Etudiant createEtudiant(@RequestBody Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Etudiant etudiant) {
        if (etudiantRepository.findByEmail(etudiant.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email étudiant déjà existant");
        }
        Etudiant saved = etudiantRepository.save(etudiant);
        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), "ETUDIANT");
        return ResponseEntity.ok(new LoginResponse(token, saved.getId(), saved.getNom(), saved.getPrenom(), saved.getEmail(), "ETUDIANT"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        if (!etudiantRepository.existsById(id)) return ResponseEntity.notFound().build();
        etudiantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Etudiant etudiant) {
        return etudiantRepository.findByEmail(etudiant.getEmail())
                .map(e -> {
                    if (e.getPassword().equals(etudiant.getPassword())) {
                        String token = jwtUtil.generateToken(e.getId(), e.getEmail(), "ETUDIANT");
                        return ResponseEntity.ok((Object) new LoginResponse(token, e.getId(), e.getNom(), e.getPrenom(), e.getEmail(), "ETUDIANT"));
                    } else {
                        return ResponseEntity.status(401).body((Object) "Mot de passe incorrect");
                    }
                })
                .orElse(ResponseEntity.status(404).body("Étudiant non trouvé"));
    }
}
