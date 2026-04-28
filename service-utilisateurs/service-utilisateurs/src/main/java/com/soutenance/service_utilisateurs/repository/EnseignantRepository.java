package com.soutenance.service_utilisateurs.repository;

import com.soutenance.service_utilisateurs.entity.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {
    // Cette interface permet de gérer les Enseignants en base de données
}