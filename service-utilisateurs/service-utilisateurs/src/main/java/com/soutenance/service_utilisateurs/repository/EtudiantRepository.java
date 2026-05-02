package com.soutenance.service_utilisateurs.repository;

import com.soutenance.service_utilisateurs.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    // Ajoutez cette ligne :
    Optional<Etudiant> findByEmail(String email);
}