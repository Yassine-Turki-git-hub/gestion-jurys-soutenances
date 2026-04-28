package com.soutenance.service_utilisateurs.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "etudiants")
@Data 
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String classe;
}