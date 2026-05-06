package tn.microservices.serviceplanification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.microservices.serviceplanification.dto.SoutenanceDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConflitService {

    /**
     * Verify if there is a conflict for a given Soutenance
     * Checks for:
     * 1. Encadrant time conflict - same encadrant in same time slot
     * 2. Jury member conflict - same jury member in same time slot
     * 3. Room conflict - same room in same time slot
     */
    public boolean verifierConflit(SoutenanceDTO soutenance,
                                   List<SoutenanceDTO> allSoutenances,
                                   Long salleId,
                                   Long creneauId) {

        if (creneauId == null || salleId == null) {
            return false;
        }

        for (SoutenanceDTO other : allSoutenances) {
            // Skip if other soutenance has no planning yet
            if (other.getCreneauId() == null || other.getSalleId() == null) {
                continue;
            }

            // Skip if it's the same soutenance
            if (other.getId().equals(soutenance.getId())) {
                continue;
            }

            // Check if same time slot
            if (!other.getCreneauId().equals(creneauId)) {
                continue;
            }

            // Check room conflict
            if (other.getSalleId().equals(salleId)) {
                return true;
            }

            // Check encadrant conflict
            if (other.getEncadrantId() != null && other.getEncadrantId().equals(soutenance.getEncadrantId())) {
                return true;
            }

            // Check jury member conflict
            if (soutenance.getJuryIds() != null && !soutenance.getJuryIds().isEmpty()) {
                if (other.getJuryIds() != null && !other.getJuryIds().isEmpty()) {
                    for (Long juryId : soutenance.getJuryIds()) {
                        if (other.getJuryIds().contains(juryId)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Detailed conflict detection with specific conflict type
     */
    public ConflitInfo detecterConflitDetaille(SoutenanceDTO soutenance,
                                               List<SoutenanceDTO> allSoutenances,
                                               Long salleId,
                                               Long creneauId) {

        if (creneauId == null || salleId == null) {
            return ConflitInfo.AUCUN;
        }

        for (SoutenanceDTO other : allSoutenances) {
            if (other.getCreneauId() == null || other.getSalleId() == null) {
                continue;
            }

            if (other.getId().equals(soutenance.getId())) {
                continue;
            }

            if (!other.getCreneauId().equals(creneauId)) {
                continue;
            }

            if (other.getSalleId().equals(salleId)) {
                return new ConflitInfo(true, "Conflit de salle : la salle est déjà réservée pour ce créneau");
            }

            if (other.getEncadrantId() != null && other.getEncadrantId().equals(soutenance.getEncadrantId())) {
                return new ConflitInfo(true, "Conflit d'encadrant : l'encadrant est déjà occupé à ce créneau");
            }

            if (soutenance.getJuryIds() != null && !soutenance.getJuryIds().isEmpty()) {
                if (other.getJuryIds() != null && !other.getJuryIds().isEmpty()) {
                    for (Long juryId : soutenance.getJuryIds()) {
                        if (other.getJuryIds().contains(juryId)) {
                            return new ConflitInfo(true, "Conflit de jury : un membre du jury est déjà occupé à ce créneau");
                        }
                    }
                }
            }
        }

        return ConflitInfo.AUCUN;
    }

    public static class ConflitInfo {
        public static final ConflitInfo AUCUN = new ConflitInfo(false, "Aucun conflit détecté");

        public boolean conflitDetecte;
        public String description;

        public ConflitInfo(boolean conflitDetecte, String description) {
            this.conflitDetecte = conflitDetecte;
            this.description = description;
        }

        public boolean hasConflit() {
            return conflitDetecte;
        }
    }
}
