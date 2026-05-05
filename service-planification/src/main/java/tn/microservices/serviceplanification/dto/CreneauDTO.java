package tn.microservices.serviceplanification.dto;

import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreneauDTO {
    private String id;
    private LocalDate date;
    private String heureDebut;
    private String heureFin;
    private Boolean estLibre;
}