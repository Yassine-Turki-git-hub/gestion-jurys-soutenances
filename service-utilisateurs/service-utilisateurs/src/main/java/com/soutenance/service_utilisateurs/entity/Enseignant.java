package com.soutenance.service_utilisateurs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "enseignants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enseignant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private String nom;
    private String prenom;

    @Column(unique = true)
    private String email;
    private String password;
    private String grade;
    private String departement;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isAdmin = false;
}