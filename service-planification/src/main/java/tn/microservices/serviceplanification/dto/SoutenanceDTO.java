package tn.microservices.serviceplanification.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoutenanceDTO {
    private String id;
    private String etudiantId;
    private String encadrantId;
    private List<String> juryIds;
    private String salleId;
    private String creneauId;
}
