package com.soutenance.service_utilisateurs.repository;

import com.soutenance.service_utilisateurs.entity.Enseignant;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {
    Optional<Enseignant> findByEmail(String email);
}