package com.soutenance.jury.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class NoteDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String membreJuryId;
        private Float valeur;
        private String commentaire;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String membreJuryId;
        private String soutenanceId;
        private Float valeur;
        private String commentaire;
        private LocalDate dateSaisie;
    }
}
