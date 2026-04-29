package com.soutenance.jury.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "note")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_jury_id", nullable = false)
    private MembreJury membreJury;

    @Column(nullable = false)
    private String soutenanceId;

    @Column(nullable = false)
    private Float valeur;

    private String commentaire;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate dateSaisie;
}
