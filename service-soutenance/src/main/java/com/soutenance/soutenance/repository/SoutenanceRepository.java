package com.soutenance.soutenance.repository;

import com.soutenance.soutenance.entity.Soutenance;
import com.soutenance.soutenance.entity.enums.StatutSoutenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SoutenanceRepository extends JpaRepository<Soutenance, String> {

    List<Soutenance> findByEtudiantId(String etudiantId);

    List<Soutenance> findByEncadrantId(String encadrantId);

    List<Soutenance> findByStatut(StatutSoutenance statut);

    List<Soutenance> findByDate(LocalDate date);

    // Vérifie si un étudiant a déjà une soutenance à une date+heure donnée
    boolean existsByEtudiantIdAndDate(String etudiantId, LocalDate date);

    // Vérifie si un encadrant a déjà une soutenance à une date donnée
    boolean existsByEncadrantIdAndDate(String encadrantId, LocalDate date);
}
