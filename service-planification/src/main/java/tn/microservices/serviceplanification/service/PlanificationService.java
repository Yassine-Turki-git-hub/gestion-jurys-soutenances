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
    public void planifierManuel(String soutenanceId, Long salleId, Long creneauId) {
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée: " + salleId));

        Creneau creneau = creneauRepository.findById(creneauId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé: " + creneauId));

        List<SoutenanceDTO> toutesLesSoutenances = soutenanceClient.getAll();
        SoutenanceDTO soutenance = toutesLesSoutenances.stream()
                .filter(s -> s.getId().equals(soutenanceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée: " + soutenanceId));

        ConflitService.ConflitInfo conflitInfo = conflitService.detecterConflitDetaille(soutenance, salle, creneau, toutesLesSoutenances);
        if (conflitInfo.hasConflit()) {
            throw new ConflitPlanificationException("Conflit de planification: " + conflitInfo.getDescription());
        }

        soutenanceClient.affecter(soutenanceId, salle.getId().toString(), creneau.getId().toString());
    }

    // ============================================
    // MANUAL BATCH PLANNING
    // ============================================
    public BatchPlanificationResult planifierLot(List<String> soutenanceIds) {
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

        for (String soutenanceId : soutenanceIds) {
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
                            soutenanceClient.affecter(soutenanceId, salle.getId().toString(), creneau.getId().toString());
                            result.ajouterSucces(soutenanceId, salle.getId(), creneau.getId());
                            soutenance.setSalleId(salle.getId().toString());
                            soutenance.setCreneauId(creneau.getId().toString());
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
                continue;
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
                        soutenanceClient.affecter(soutenance.getId(), salle.getId().toString(), creneau.getId().toString());
                        result.incrementerReussites();
                        soutenance.setSalleId(salle.getId().toString());
                        soutenance.setCreneauId(creneau.getId().toString());
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
    public void modifierPlanification(String soutenanceId, Long newSalleId, Long newCreneauId) {
        Salle newSalle = salleRepository.findById(newSalleId)
                .orElseThrow(() -> new ResourceNotFoundException("Nouvelle salle non trouvée: " + newSalleId));

        Creneau newCreneau = creneauRepository.findById(newCreneauId)
                .orElseThrow(() -> new ResourceNotFoundException("Nouveau créneau non trouvé: " + newCreneauId));

        List<SoutenanceDTO> toutesLesSoutenances = soutenanceClient.getAll();
        SoutenanceDTO soutenance = toutesLesSoutenances.stream()
                .filter(s -> s.getId().equals(soutenanceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée: " + soutenanceId));

        if (soutenance.getSalleId() == null || soutenance.getCreneauId() == null) {
            throw new ConflitPlanificationException("La soutenance n'est pas encore planifiée");
        }

        String oldSalleId = soutenance.getSalleId();
        String oldCreneauId = soutenance.getCreneauId();
        soutenance.setSalleId(null);
        soutenance.setCreneauId(null);

        ConflitService.ConflitInfo conflitInfo = conflitService.detecterConflitDetaille(soutenance, newSalle, newCreneau, toutesLesSoutenances);

        if (conflitInfo.hasConflit()) {
            soutenance.setSalleId(oldSalleId);
            soutenance.setCreneauId(oldCreneauId);
            throw new ConflitPlanificationException("Conflit détecté pour la nouvelle planification: " + conflitInfo.getDescription());
        }

        soutenanceClient.affecter(soutenanceId, newSalle.getId().toString(), newCreneau.getId().toString());
    }

    // ============================================
    // DELETE PLANNING
    // ============================================
    public void supprimerPlanification(String soutenanceId) {
        List<SoutenanceDTO> toutesLesSoutenances = soutenanceClient.getAll();

        SoutenanceDTO soutenance = toutesLesSoutenances.stream()
                .filter(s -> s.getId().equals(soutenanceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée: " + soutenanceId));

        if (soutenance.getSalleId() == null || soutenance.getCreneauId() == null) {
            throw new ConflitPlanificationException("La soutenance n'est pas planifiée");
        }

        soutenanceClient.supprimerPlanification(soutenanceId);
    }

    // ============================================
    // PLAN ONE SPECIFIC SOUTENANCE
    // ============================================
    public SoutenancePlanResult planifierSoutenance(String soutenanceId) {
        List<Salle> salles = salleRepository.findAll();
        List<Creneau> creneaux = creneauRepository.findAll();
        List<SoutenanceDTO> toutesLesSoutenances = soutenanceClient.getAll();

        if (salles.isEmpty() || creneaux.isEmpty()) {
            throw new ConflitPlanificationException("Pas de salles ou créneaux disponibles");
        }

        SoutenanceDTO soutenance = toutesLesSoutenances.stream()
                .filter(s -> s.getId().equals(soutenanceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance non trouvée: " + soutenanceId));

        if (soutenance.getSalleId() != null && soutenance.getCreneauId() != null) {
            return new SoutenancePlanResult(
                    Long.parseLong(soutenance.getSalleId()),
                    Long.parseLong(soutenance.getCreneauId()));
        }

        for (Salle salle : salles) {
            for (Creneau creneau : creneaux) {
                ConflitService.ConflitInfo conflit = conflitService.detecterConflitDetaille(
                        soutenance, salle, creneau, toutesLesSoutenances);
                if (!conflit.hasConflit()) {
                    soutenanceClient.affecter(soutenanceId, salle.getId().toString(), creneau.getId().toString());
                    return new SoutenancePlanResult(salle.getId(), creneau.getId());
                }
            }
        }

        throw new ConflitPlanificationException("Aucune combinaison salle/créneau disponible pour cette soutenance");
    }

    // ============================================
    // RESULT CLASSES
    // ============================================

    public static class SoutenancePlanResult {
        private final Long salleId;
        private final Long creneauId;

        public SoutenancePlanResult(Long salleId, Long creneauId) {
            this.salleId = salleId;
            this.creneauId = creneauId;
        }

        public Long getSalleId()   { return salleId; }
        public Long getCreneauId() { return creneauId; }
    }

    public static class BatchPlanificationResult {
        private List<String> reussies = new ArrayList<>();
        private List<String> echouees = new ArrayList<>();
        private List<String> ignorees = new ArrayList<>();
        private Map<String, String> erreurs = new HashMap<>();
        private Map<String, Map<String, Long>> details = new HashMap<>();

        public void ajouterSucces(String soutenanceId, Long salleId, Long creneauId) {
            reussies.add(soutenanceId);
            Map<String, Long> detail = new HashMap<>();
            detail.put("salleId", salleId);
            detail.put("creneauId", creneauId);
            details.put(soutenanceId, detail);
        }

        public void ajouterEchec(String soutenanceId, String raison) {
            echouees.add(soutenanceId);
            erreurs.put(soutenanceId, raison);
        }

        public void ajouterIgnoree(String soutenanceId, String raison) {
            ignorees.add(soutenanceId);
            erreurs.put(soutenanceId, raison);
        }

        public List<String> getReussies() { return reussies; }
        public List<String> getEchouees() { return echouees; }
        public List<String> getIgnorees() { return ignorees; }
        public Map<String, String> getErreurs() { return erreurs; }
        public Map<String, Map<String, Long>> getDetails() { return details; }
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
