package tn.microservices.serviceplanification.dto;

import lombok.Data;
import java.util.List;

@Data
public class SoutenanceDTO {

    private Long id;

    private Long etudiantId;
    private Long encadrantId;

    private List<Long> juryIds;

    private Long salleId;
    private Long creneauId;
}
