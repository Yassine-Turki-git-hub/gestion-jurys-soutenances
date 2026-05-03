package com.soutenance.soutenance.entity;

import com.soutenance.soutenance.entity.enums.StatutSoutenance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "soutenances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Soutenance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    // Références vers autres services (pas de @ManyToOne cross-service)
    @Column(nullable = false)
    private String etudiantId;

    @Column(nullable = false)
    private String encadrantId;

    // Optionnel — rempli après planification
    private String salleId;
    private String creneauId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSoutenance statut = StatutSoutenance.EN_ATTENTE;
}
