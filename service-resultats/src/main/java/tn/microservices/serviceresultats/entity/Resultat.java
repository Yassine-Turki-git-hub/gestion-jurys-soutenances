package tn.microservices.serviceresultats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "resultats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resultat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String soutenanceId;

    private Double moyenne;

    private String mention; // Passable, Assez Bien, Bien, Très Bien

    private Boolean valide;

    private LocalDateTime dateCalcul;
}
