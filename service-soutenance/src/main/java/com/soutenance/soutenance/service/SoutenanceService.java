package com.soutenance.soutenance.service;

import com.soutenance.soutenance.client.PlanificationClient;
import com.soutenance.soutenance.client.UtilisateurClient;
import com.soutenance.soutenance.dto.SoutenanceDTO;
import com.soutenance.soutenance.entity.Soutenance;
import com.soutenance.soutenance.entity.enums.StatutSoutenance;
import com.soutenance.soutenance.exception.ConflitHoraireException;
import com.soutenance.soutenance.exception.ResourceNotFoundException;
import com.soutenance.soutenance.repository.SoutenanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SoutenanceService {

    private final SoutenanceRepository soutenanceRepository;
    private final UtilisateurClient utilisateurClient;
    private final PlanificationClient planificationClient;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Transactional
    public SoutenanceDTO creer(SoutenanceDTO dto) {

        // 1. Vérifier que l'étudiant existe (via Feign → service-utilisateurs)
        Boolean etudiantExiste = utilisateurClient.etudiantExists(Long.parseLong(dto.getEtudiantId()));
        if (!Boolean.TRUE.equals(etudiantExiste)) {
            throw new ResourceNotFoundException("Étudiant introuvable : " + dto.getEtudiantId());
        }

        // 2. Vérifier que l'encadrant existe (via Feign → service-utilisateurs)
        Boolean encadrantExiste = utilisateurClient.encadrantExists(Long.parseLong(dto.getEncadrantId()));
        if (!Boolean.TRUE.equals(encadrantExiste)) {
            throw new ResourceNotFoundException("Encadrant introuvable : " + dto.getEncadrantId());
        }

        // 3 & 4. Vérifier les conflits horaires (création : excludeId = "none")
        verifierConflits(dto.getEtudiantId(), dto.getEncadrantId(), dto.getSalleId(),
                         dto.getDate(), dto.getHeureDebut(), dto.getHeureFin(),
                         /* excludeId = */ "none");

        // 5. Vérifier disponibilité salle et créneau via Feign (inchangé)
        if (dto.getSalleId() != null && !dto.getSalleId().isBlank()) {
            Boolean salleDisponible = planificationClient.salleDisponible(dto.getSalleId());
            if (!Boolean.TRUE.equals(salleDisponible)) {
                throw new ConflitHoraireException("La salle " + dto.getSalleId() + " n'est pas disponible.");
            }
        }
        if (dto.getCreneauId() != null && !dto.getCreneauId().isBlank()) {
            Boolean creneauDisponible = planificationClient.creneauDisponible(dto.getCreneauId());
            if (!Boolean.TRUE.equals(creneauDisponible)) {
                throw new ConflitHoraireException("Le créneau " + dto.getCreneauId() + " n'est pas disponible.");
            }
        }

        // 6. Sauvegarder
        Soutenance soutenance = toEntity(dto);
        soutenance.setStatut(StatutSoutenance.PLANIFIEE);
        Soutenance saved = soutenanceRepository.save(soutenance);
        log.info("Soutenance créée : {}", saved.getId());
        return toDto(saved);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<SoutenanceDTO> getAll() {
        return soutenanceRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public SoutenanceDTO getById(String id) {
        return toDto(findOrThrow(id));
    }

    public List<SoutenanceDTO> getByEtudiant(String etudiantId) {
        return soutenanceRepository.findByEtudiantId(etudiantId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SoutenanceDTO> getByEncadrant(String encadrantId) {
        return soutenanceRepository.findByEncadrantId(encadrantId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Transactional
    public SoutenanceDTO modifier(String id, SoutenanceDTO dto) {
        Soutenance existing = findOrThrow(id);

        // Re-check conflicts, but exclude the soutenance being edited
        // so it doesn't conflict with itself.
        verifierConflits(dto.getEtudiantId(), dto.getEncadrantId(), dto.getSalleId(),
                         dto.getDate(), dto.getHeureDebut(), dto.getHeureFin(),
                         /* excludeId = */ id);

        existing.setTitre(dto.getTitre());
        existing.setDate(dto.getDate());
        existing.setHeureDebut(dto.getHeureDebut());
        existing.setHeureFin(dto.getHeureFin());
        existing.setSalleId(dto.getSalleId());
        existing.setCreneauId(dto.getCreneauId());
        return toDto(soutenanceRepository.save(existing));
    }

    // ── STATUS TRANSITIONS ────────────────────────────────────────────────────

    @Transactional
    public SoutenanceDTO annuler(String id) {
        Soutenance s = findOrThrow(id);
        if (s.getStatut() == StatutSoutenance.TERMINEE) {
            throw new IllegalArgumentException("Impossible d'annuler une soutenance déjà terminée.");
        }
        s.setStatut(StatutSoutenance.ANNULEE);
        return toDto(soutenanceRepository.save(s));
    }

    @Transactional
    public SoutenanceDTO valider(String id) {
        Soutenance s = findOrThrow(id);
        s.setStatut(StatutSoutenance.TERMINEE);
        return toDto(soutenanceRepository.save(s));
    }

    @Transactional
    public SoutenanceDTO demarrer(String id) {
        Soutenance s = findOrThrow(id);
        if (s.getStatut() != StatutSoutenance.PLANIFIEE) {
            throw new IllegalArgumentException("La soutenance doit être PLANIFIEE pour démarrer.");
        }
        s.setStatut(StatutSoutenance.EN_COURS);
        return toDto(soutenanceRepository.save(s));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Transactional
    public void supprimer(String id) {
        findOrThrow(id);
        soutenanceRepository.deleteById(id);
    }

    // ── PRIVATE: CONFLICT VALIDATION ──────────────────────────────────────────

    /**
     * Central time-overlap check used by both creer() and modifier().
     *
     * Overlap condition: newDebut < existingFin  AND  newFin > existingDebut
     *
     * @param excludeId  the ID of the soutenance to ignore (pass "none" on creation,
     *                   pass the real ID on update so a record doesn't conflict with itself).
     */
    private void verifierConflits(String etudiantId, String encadrantId, String salleId,
                                  LocalDate date, LocalTime heureDebut, LocalTime heureFin,
                                  String excludeId) {

        if (soutenanceRepository.existsConflitEtudiant(etudiantId, date, heureDebut, heureFin, excludeId)) {
            throw new ConflitHoraireException(
                "L'étudiant " + etudiantId
                + " a déjà une soutenance qui chevauche le créneau "
                + heureDebut + "–" + heureFin + " le " + date + "."
            );
        }

        if (soutenanceRepository.existsConflitEncadrant(encadrantId, date, heureDebut, heureFin, excludeId)) {
            throw new ConflitHoraireException(
                "L'encadrant " + encadrantId
                + " a déjà une soutenance qui chevauche le créneau "
                + heureDebut + "–" + heureFin + " le " + date + "."
            );
        }

        if (salleId != null && !salleId.isBlank()) {
            if (soutenanceRepository.existsConflitSalle(salleId, date, heureDebut, heureFin, excludeId)) {
                throw new ConflitHoraireException(
                    "La salle " + salleId
                    + " est déjà occupée sur le créneau "
                    + heureDebut + "–" + heureFin + " le " + date + "."
                );
            }
        }
    }

    @Transactional
    public SoutenanceDTO supprimerPlanification(String id) {
        SoutenanceDTO soutenance = getById(id);

        if (soutenance.getSalleId() == null && soutenance.getCreneauId() == null) {
            throw new RuntimeException("La soutenance n'est pas planifiée");
        }

        soutenance.setSalleId(null);
        soutenance.setCreneauId(null);

        return modifier(id, soutenance);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private Soutenance findOrThrow(String id) {
        return soutenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance introuvable : " + id));
    }

    private SoutenanceDTO toDto(Soutenance s) {
        SoutenanceDTO dto = new SoutenanceDTO();
        dto.setId(s.getId());
        dto.setTitre(s.getTitre());
        dto.setDate(s.getDate());
        dto.setHeureDebut(s.getHeureDebut());
        dto.setHeureFin(s.getHeureFin());
        dto.setEtudiantId(s.getEtudiantId());
        dto.setEncadrantId(s.getEncadrantId());
        dto.setSalleId(s.getSalleId());
        dto.setCreneauId(s.getCreneauId());
        dto.setStatut(s.getStatut());
        return dto;
    }

    private Soutenance toEntity(SoutenanceDTO dto) {
        Soutenance s = new Soutenance();
        s.setTitre(dto.getTitre());
        s.setDate(dto.getDate());
        s.setHeureDebut(dto.getHeureDebut());
        s.setHeureFin(dto.getHeureFin());
        s.setEtudiantId(dto.getEtudiantId());
        s.setEncadrantId(dto.getEncadrantId());
        s.setSalleId(dto.getSalleId());
        s.setCreneauId(dto.getCreneauId());
        return s;
    }
}