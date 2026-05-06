package tn.microservices.serviceplanification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.microservices.serviceplanification.dto.SoutenanceDTO;
import tn.microservices.serviceplanification.entity.Creneau;
import tn.microservices.serviceplanification.entity.Salle;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConflitService {

    public boolean verifierConflit(SoutenanceDTO soutenance,
                                   List<SoutenanceDTO> allSoutenances,
                                   Long salleId,
                                   Long creneauId) {

        if (creneauId == null || salleId == null) {
            return false;
        }

        String salleIdStr = salleId.toString();
        String creneauIdStr = creneauId.toString();

        for (SoutenanceDTO other : allSoutenances) {
            if (other.getCreneauId() == null || other.getSalleId() == null) {
                continue;
            }
            if (other.getId().equals(soutenance.getId())) {
                continue;
            }
            if (!other.getCreneauId().equals(creneauIdStr)) {
                continue;
            }
            if (other.getSalleId().equals(salleIdStr)) {
                return true;
            }
            if (other.getEncadrantId() != null && other.getEncadrantId().equals(soutenance.getEncadrantId())) {
                return true;
            }
            if (soutenance.getJuryIds() != null && !soutenance.getJuryIds().isEmpty()) {
                if (other.getJuryIds() != null && !other.getJuryIds().isEmpty()) {
                    for (String juryId : soutenance.getJuryIds()) {
                        if (other.getJuryIds().contains(juryId)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ConflitInfo detecterConflitDetaille(SoutenanceDTO soutenance,
                                               Salle salle,
                                               Creneau creneau,
                                               List<SoutenanceDTO> allSoutenances) {

        if (creneau == null || salle == null) {
            return ConflitInfo.AUCUN;
        }

        String salleIdStr = salle.getId().toString();
        String creneauIdStr = creneau.getId().toString();

        for (SoutenanceDTO other : allSoutenances) {
            if (other.getCreneauId() == null || other.getSalleId() == null) {
                continue;
            }
            if (other.getId().equals(soutenance.getId())) {
                continue;
            }
            if (!other.getCreneauId().equals(creneauIdStr)) {
                continue;
            }
            if (other.getSalleId().equals(salleIdStr)) {
                return new ConflitInfo(true, "Conflit de salle : la salle est déjà réservée pour ce créneau");
            }
            if (other.getEncadrantId() != null && other.getEncadrantId().equals(soutenance.getEncadrantId())) {
                return new ConflitInfo(true, "Conflit d'encadrant : l'encadrant est déjà occupé à ce créneau");
            }
            if (soutenance.getJuryIds() != null && !soutenance.getJuryIds().isEmpty()) {
                if (other.getJuryIds() != null && !other.getJuryIds().isEmpty()) {
                    for (String juryId : soutenance.getJuryIds()) {
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

        public String getDescription() {
            return description;
        }
    }
}
