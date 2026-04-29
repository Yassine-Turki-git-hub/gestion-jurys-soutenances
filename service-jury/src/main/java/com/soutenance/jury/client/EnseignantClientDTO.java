package com.soutenance.jury.client;

import lombok.Data;

@Data
public class EnseignantClientDTO {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String grade;
    private String departement;
}
