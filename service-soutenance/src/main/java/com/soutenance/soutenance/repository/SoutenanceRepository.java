package com.soutenance.soutenance.repository;

import com.soutenance.soutenance.entity.Soutenance;
import com.soutenance.soutenance.entity.enums.StatutSoutenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SoutenanceRepository extends JpaRepository<Soutenance, String> {

    List<Soutenance> findByEtudiantId(String etudiantId);

    List<Soutenance> findByEncadrantId(String encadrantId);

    List<Soutenance> findByStatut(StatutSoutenance statut);

    List<Soutenance> findByDate(LocalDate date);

    // ── CONFLICT CHECKS (time overlap: A.debut < B.fin AND A.fin > B.debut) ──

    /**
     * Returns true if the student already has a soutenance on the same date
     * whose time range overlaps with [heureDebut, heureFin).
     * ANNULEE soutenances are excluded (they no longer occupy the slot).
     * The excludeId parameter lets modifier() ignore the soutenance being edited.
     */
    @Query("""
        SELECT COUNT(s) > 0 FROM Soutenance s
        WHERE s.etudiantId = :etudiantId
          AND s.date       = :date
          AND s.statut    <> com.soutenance.soutenance.entity.enums.StatutSoutenance.ANNULEE
          AND s.id        <> :excludeId
          AND s.heureDebut < :heureFin
          AND s.heureFin  > :heureDebut
    """)
    boolean existsConflitEtudiant(
            @Param("etudiantId") String etudiantId,
            @Param("date")       LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin")   LocalTime heureFin,
            @Param("excludeId")  String excludeId
    );

    /**
     * Same logic for the encadrant (supervisor).
     */
    @Query("""
        SELECT COUNT(s) > 0 FROM Soutenance s
        WHERE s.encadrantId = :encadrantId
          AND s.date        = :date
          AND s.statut     <> com.soutenance.soutenance.entity.enums.StatutSoutenance.ANNULEE
          AND s.id         <> :excludeId
          AND s.heureDebut  < :heureFin
          AND s.heureFin   > :heureDebut
    """)
    boolean existsConflitEncadrant(
            @Param("encadrantId") String encadrantId,
            @Param("date")        LocalDate date,
            @Param("heureDebut")  LocalTime heureDebut,
            @Param("heureFin")    LocalTime heureFin,
            @Param("excludeId")   String excludeId
    );

    /**
     * Room conflict: same salle, same date, overlapping time range.
     */
    @Query("""
        SELECT COUNT(s) > 0 FROM Soutenance s
        WHERE s.salleId  = :salleId
          AND s.date     = :date
          AND s.statut  <> com.soutenance.soutenance.entity.enums.StatutSoutenance.ANNULEE
          AND s.id      <> :excludeId
          AND s.heureDebut < :heureFin
          AND s.heureFin  > :heureDebut
    """)
    boolean existsConflitSalle(
            @Param("salleId")    String salleId,
            @Param("date")       LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin")   LocalTime heureFin,
            @Param("excludeId")  String excludeId
    );
}