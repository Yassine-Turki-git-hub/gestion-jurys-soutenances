package tn.microservices.serviceplanification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.microservices.serviceplanification.client.SoutenanceClient;
import tn.microservices.serviceplanification.dto.SoutenanceDTO;
import tn.microservices.serviceplanification.entity.Creneau;
import tn.microservices.serviceplanification.entity.Salle;
import tn.microservices.serviceplanification.exception.ConflitPlanificationException;
import tn.microservices.serviceplanification.exception.ResourceNotFoundException;
import tn.microservices.serviceplanification.repository.CreneauRepository;
import tn.microservices.serviceplanification.repository.SalleRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanificationService {

    private final SalleRepository salleRepository;
    private final CreneauRepository creneauRepository;
    private final SoutenanceClient soutenanceClient;
    private final ConflitService conflitService;

    // ============================================
    // MANUAL SINGLE PLANNING
    // ============================================
    public void planifierManuel(Long soutenanceId, Long salleId, Long creneauId) {
        // Validate that salle and creneau exist
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée: " + salleId));

        Creneau creneau = creneauRepository.findById(creneauId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé: " + creneauId));

        // Get all soutenances to check conflicts
        List<SoutenanceDTO> toutesLesSoutenances = soutenanceClient.getAll();
        SoutenanceDTO soutenance = toutesLesSoutenances.stream()
                .filter(s -> s.getId().equals(soutenanceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée: " + soutenanceId));

        // Check for conflicts
        ConflitService.ConflitInfo conflitInfo = conflitService.detecterConflitDetaille(soutenance, salle, creneau, toutesLesSoutenances);
        if (conflitInfo.hasConflit()) {
            throw new ConflitPlanificationException("Conflit de planification: " + conflitInfo.getDescription());
        }

        // Assign the soutenance
        soutenanceClient.affecter(soutenanceId, salleId, creneauId);
    }

    // ============================================
    // MANUAL BATCH PLANNING (Multiple soutenances)
    // ============================================
    public BatchPlanificationResult planifierLot(List<Long> soutenanceIds) {
        if (soutenanceIds == null || soutenanceIds.isEmpty()) {
            throw new IllegalArgumentException("La liste des soutenances ne peut pas être vide");
        }

        BatchPlanificationResult result = new BatchPlanificationResult();
        List<Salle> salles = salleRepository.findAll();
        List<Creneau> creneaux = creneauRepository.findAll();
        List<SoutenanceDTO> toutesLesSoutenances = soutenanceClient.getAll();

        if (salles.isEmpty() || creneaux.isEmpty()) {
            throw new ConflitPlanificationException("Pas assez de salles ou de créneaux disponibles");
        }

        for (Long soutenanceId : soutenanceIds) {
            SoutenanceDTO soutenance = toutesLesSoutenances.stream()
                    .filter(s -> s.getId().equals(soutenanceId))
                    .findFirst()
                    .orElse(null);

            if (soutenance == null) {
                result.ajouterEchec(soutenanceId, "Soutenance non trouvée");
                continue;
            }

            if (soutenance.getSalleId() != null && soutenance.getCreneauId() != null) {
                result.ajouterIgnoree(soutenanceId, "Déjà planifiée");
                continue;
            }

            boolean planifiee = false;
            for (Salle salle : salles) {
                if (planifiee) break;

                for (Creneau creneau : creneaux) {
                    ConflitService.ConflitInfo conflitInfo = conflitService.detecterConflitDetaille(soutenance, salle, creneau, toutesLesSoutenances);

                    if (!conflitInfo.hasConflit()) {
                        try {
                            soutenanceClient.affecter(soutenanceId, salle.getId(), creneau.getId());
                            result.ajouterSucces(soutenanceId, salle.getId(), creneau.getId());

                            // Update local list for subsequent conflict checks
                            soutenance.setSalleId(salle.getId());
                            soutenance.setCreneauId(creneau.getId());

                            planifiee = true;
                            break;
                        } catch (Exception e) {
                            result.ajouterEchec(soutenanceId, "Erreur lors de l'affectation: " + e.getMessage());
                            planifiee = true;
                            break;
                        }
                    }
                }
            }

            if (!planifiee) {
                result.ajouterEchec(soutenanceId, "Aucune combinaison salle/créneau disponible");
            }
        }

        return result;
    }

    // ============================================
    // AUTOMATIC PLANNING
    // ============================================
    public AutoPlanificationResult planifierAuto() {
        AutoPlanificationResult result = new AutoPlanificationResult();

        List<Salle> salles = salleRepository.findAll();
        List<Creneau> creneaux = creneauRepository.findAll();
        List<SoutenanceDTO> toutesLesSoutenances = soutenanceClient.getAll();

        if (salles.isEmpty() || creneaux.isEmpty()) {
            result.setMessage("Pas assez de salles ou de créneaux disponibles");
            return result;
        }

        int salleIndex = 0;
        int creneauIndex = 0;

        for (SoutenanceDTO soutenance : toutesLesSoutenances) {
            if (soutenance.getSalleId() != null && soutenance.getCreneauId() != null) {
                continue; // Already planned
            }

            int attempts = 0;
            int maxAttempts = salles.size() * creneaux.size();
            boolean planned = false;

            while (attempts < maxAttempts && !planned) {
                Salle salle = salles.get(salleIndex % salles.size());
                Creneau creneau = creneaux.get(creneauIndex % creneaux.size());

                ConflitService.ConflitInfo conflitInfo = conflitService.detecterConflitDetaille(soutenance, salle, creneau, toutesLesSoutenances);

                if (!conflitInfo.hasConflit()) {
                    try {
                        soutenanceClient.affecter(soutenance.getId(), salle.getId(), creneau.getId());
                        result.incrementerReussites();
                        soutenance.setSalleId(salle.getId());
                        soutenance.setCreneauId(creneau.getId());
                        planned = true;
                    } catch (Exception e) {
                        result.incrementerEchecs();
                        planned = true;
                    }
                }

                salleIndex++;
                creneauIndex++;
                attempts++;
            }

            if (!planned) {
                result.incrementerNonPlanifiees();
            }
        }

        result.setMessage("Planification automatique terminée");
        return result;
    }

    // ============================================
    // MODIFY PLANNING
    // ============================================
    public void modifierPlanification(Long soutenanceId, Long newSalleId, Long newCreneauId) {
        final Long finalSalleId = newSalleId;
        final Long finalCreneauId = newCreneauId;

        // Validate new salle and creneau exist
        Salle newSalle = salleRepository.findById(finalSalleId)
                .orElseThrow(() -> new ResourceNotFoundException("Nouvelle salle non trouvée: " + finalSalleId));

        Creneau newCreneau = creneauRepository.findById(finalCreneauId)
                .orElseThrow(() -> new ResourceNotFoundException("Nouveau créneau non trouvé: " + finalCreneauId));

        List<SoutenanceDTO> toutesLesSoutenances = soutenanceClient.getAll();
        SoutenanceDTO soutenance = toutesLesSoutenances.stream()
                .filter(s -> s.getId().equals(soutenanceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée: " + soutenanceId));

        if (soutenance.getSalleId() == null || soutenance.getCreneauId() == null) {
            throw new ConflitPlanificationException("La soutenance n'est pas encore planifiée");
        }

        // Temporarily remove current planning to check conflicts for new slot
        Long oldSalleId = soutenance.getSalleId();
        Long oldCreneauId = soutenance.getCreneauId();
        soutenance.setSalleId(null);
        soutenance.setCreneauId(null);

        // Check conflicts with new slot
        ConflitService.ConflitInfo conflitInfo = conflitService.detecterConflitDetaille(soutenance, newSalle, newCreneau, toutesLesSoutenances);

        if (conflitInfo.hasConflit()) {
            // Restore old values
            soutenance.setSalleId(oldSalleId);
            soutenance.setCreneauId(oldCreneauId);
            throw new ConflitPlanificationException("Conflit détecté pour la nouvelle planification: " + conflitInfo.getDescription());
        }

        // Apply the modification
        soutenanceClient.affecter(soutenanceId, finalSalleId, finalCreneauId);
    }

    // ============================================
    // DELETE PLANNING
    // ============================================
    public void supprimerPlanification(Long soutenanceId) {
        List<SoutenanceDTO> toutesLesSoutenances = soutenanceClient.getAll();
        SoutenanceDTO soutenance = toutesLesSoutenances.stream()
                .filter(s -> s.getId().equals(soutenanceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée: " + soutenanceId));

        if (soutenance.getSalleId() == null || soutenance.getCreneauId() == null) {
            throw new ConflitPlanificationException("La soutenance n'est pas planifiée");
        }

        // Delete planning via API Gateway
        soutenanceClient.supprimerPlanification(soutenanceId);
    }

    // ============================================
    // RESULT CLASSES
    // ============================================

    public static class BatchPlanificationResult {
        private List<Long> reussies = new ArrayList<>();
        private List<Long> echouees = new ArrayList<>();
        private List<Long> ignorees = new ArrayList<>();
        private Map<Long, String> erreurs = new HashMap<>();
        private Map<Long, Map<String, Long>> details = new HashMap<>();

        public void ajouterSucces(Long soutenanceId, Long salleId, Long creneauId) {
            reussies.add(soutenanceId);
            Map<String, Long> detail = new HashMap<>();
            detail.put("salleId", salleId);
            detail.put("creneauId", creneauId);
            details.put(soutenanceId, detail);
        }

        public void ajouterEchec(Long soutenanceId, String raison) {
            echouees.add(soutenanceId);
            erreurs.put(soutenanceId, raison);
        }

        public void ajouterIgnoree(Long soutenanceId, String raison) {
            ignorees.add(soutenanceId);
            erreurs.put(soutenanceId, raison);
        }

        public List<Long> getReussies() { return reussies; }
        public List<Long> getEchouees() { return echouees; }
        public List<Long> getIgnorees() { return ignorees; }
        public Map<Long, String> getErreurs() { return erreurs; }
        public Map<Long, Map<String, Long>> getDetails() { return details; }
    }

    public static class AutoPlanificationResult {
        private int reussites = 0;
        private int echecs = 0;
        private int nonPlanifiees = 0;
        private String message;

        public void incrementerReussites() { this.reussites++; }
        public void incrementerEchecs() { this.echecs++; }
        public void incrementerNonPlanifiees() { this.nonPlanifiees++; }

        public int getReussites() { return reussites; }
        public int getEchecs() { return echecs; }
        public int getNonPlanifiees() { return nonPlanifiees; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
