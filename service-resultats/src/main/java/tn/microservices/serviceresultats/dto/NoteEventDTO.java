package tn.microservices.serviceresultats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteEventDTO {

    private String soutenanceId;
    private List<Double> notes;
}
