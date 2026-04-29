package com.soutenance.jury.repository;

import com.soutenance.jury.entity.MembreJury;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembreJuryRepository extends JpaRepository<MembreJury, String> {
    List<MembreJury> findBySoutenanceId(String soutenanceId);
    boolean existsByEnseignantIdAndSoutenanceId(String enseignantId, String soutenanceId);
    long countBySoutenanceId(String soutenanceId);
}
