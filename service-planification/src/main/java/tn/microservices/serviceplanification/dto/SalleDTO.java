package tn.microservices.serviceplanification.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class SalleDTO {
    private String id;
    private String numero;
    private String batiment;
    private Integer capacite;
    private Boolean estLibre;
}