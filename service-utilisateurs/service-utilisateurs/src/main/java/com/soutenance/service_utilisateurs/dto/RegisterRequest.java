package com.soutenance.service_utilisateurs.dto;

public record RegisterRequest(
    Long id, 
    String nom, 
    String prenom, 
    String email, 
    String password,
    String specialite // Optionnel : grade pour enseignant ou classe pour étudiant
) {}