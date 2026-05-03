package com.soutenance.soutenance.dto;

import com.soutenance.soutenance.entity.enums.StatutSoutenance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoutenanceDTO {

    private String id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @NotBlank(message = "L'ID étudiant est obligatoire")
    private String etudiantId;

    @NotBlank(message = "L'ID encadrant est obligatoire")
    private String encadrantId;

    private String salleId;
    private String creneauId;

    private StatutSoutenance statut;
}
