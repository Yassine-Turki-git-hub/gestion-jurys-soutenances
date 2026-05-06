package com.soutenance.service_utilisateurs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
}
