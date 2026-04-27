package tn.microservices.serviceresultats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultatDTO {

    private String soutenanceId;
    private Double moyenne;
    private String mention;
    private Boolean valide;
    private LocalDateTime dateCalcul;
}
