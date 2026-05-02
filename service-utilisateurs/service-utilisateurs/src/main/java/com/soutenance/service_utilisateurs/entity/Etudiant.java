package com.soutenance.service_utilisateurs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "etudiants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;
    private String nom;
    private String prenom;
    private String email;
    private String classe;
}