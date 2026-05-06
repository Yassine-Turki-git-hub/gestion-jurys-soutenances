package tn.microservices.serviceplanification.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoutenanceDTO {
    private Long id;
    private Long etudiantId;
    private Long encadrantId;
    private List<Long> juryIds;
    private Long salleId;
    private Long creneauId;
}
