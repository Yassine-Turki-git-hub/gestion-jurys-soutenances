package tn.microservices.serviceplanification.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalleDTO {
    private Long id;
    private String numero;
    private String batiment;
    private Integer capacite;
}
