package com.soutenance.service_utilisateurs.repository;

import com.soutenance.service_utilisateurs.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    // Grace à JpaRepository, tu as déjà accès aux méthodes :
    // .save(), .findAll(), .findById(), .delete()
}