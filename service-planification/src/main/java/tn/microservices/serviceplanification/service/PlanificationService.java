package tn.microservices.serviceplanification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.microservices.serviceplanification.client.SoutenanceClient;
import tn.microservices.serviceplanification.dto.SoutenanceDTO;
import tn.microservices.serviceplanification.entity.Creneau;
import tn.microservices.serviceplanification.entity.Salle;
import tn.microservices.serviceplanification.repository.CreneauRepository;
import tn.microservices.serviceplanification.repository.SalleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanificationService {

    private final SalleRepository salleRepository;
    private final CreneauRepository creneauRepository;
    private final SoutenanceClient soutenanceClient;

    // 🔹 MANUAL PLANNING
    public void planifierManuel(Long soutenanceId,
                                Long salleId,
                                Long creneauId) {

        soutenanceClient.affecter(soutenanceId, salleId, creneauId);
    }

    // 🔥 AUTOMATIC PLANNING
    public void planifierAuto() {

        List<SoutenanceDTO> soutenances = soutenanceClient.getAll();
        List<Salle> salles = salleRepository.findAll();
        List<Creneau> creneaux = creneauRepository.findAll();

        int i = 0;

        for (SoutenanceDTO s : soutenances) {

            if (s.getSalleId() != null) continue;

            for (Creneau c : creneaux) {

                boolean conflict = false;

                for (SoutenanceDTO other : soutenances) {

                    if (other.getCreneauId() == null) continue;

                    if (other.getCreneauId().equals(c.getId())) {

                        // encadrant conflict
                        if (other.getEncadrantId().equals(s.getEncadrantId())) {
                            conflict = true;
                            break;
                        }

                        // jury conflict
                        for (Long j : s.getJuryIds()) {
                            if (other.getJuryIds().contains(j)) {
                                conflict = true;
                                break;
                            }
                        }
                    }
                }

                if (conflict) continue;

                Salle salle = salles.get(i % salles.size());

                soutenanceClient.affecter(
                        s.getId(),
                        salle.getId(),
                        c.getId()
                );

                i++;
                break;
            }
        }
    }
}
