package com.soutenance.jury.entity;

import com.soutenance.jury.entity.enums.RoleJury;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "membre_jury")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembreJury {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String enseignantId;

    @Column(nullable = false)
    private String soutenanceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleJury role;
}
