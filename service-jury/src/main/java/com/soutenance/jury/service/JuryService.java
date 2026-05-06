package com.soutenance.jury.service;

import com.soutenance.jury.client.EnseignantClientDTO;
import com.soutenance.jury.client.UtilisateurClient;
import com.soutenance.jury.dto.JuryDTO;
import com.soutenance.jury.entity.MembreJury;
import com.soutenance.jury.entity.enums.RoleJury;
import com.soutenance.jury.repository.MembreJuryRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JuryService {

    private final MembreJuryRepository membreJuryRepository;
    private final UtilisateurClient utilisateurClient;

    public JuryDTO.Response affecterMembre(JuryDTO.Request request) {
        try {
            utilisateurClient.getEnseignantById(request.getEnseignantId());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Enseignant introuvable : " + request.getEnseignantId());
        }

        if (membreJuryRepository.existsByEnseignantIdAndSoutenanceId(
                request.getEnseignantId(), request.getSoutenanceId())) {
            throw new IllegalArgumentException("Cet enseignant est déjà affecté à cette soutenance");
        }

        if (membreJuryRepository.countBySoutenanceId(request.getSoutenanceId()) >= 3) {
            throw new IllegalArgumentException("Cette soutenance a déjà 3 membres de jury");
        }

        MembreJury membre = MembreJury.builder()
                .enseignantId(request.getEnseignantId())
                .soutenanceId(request.getSoutenanceId())
                .role(request.getRole())
                .build();

        return toResponse(membreJuryRepository.save(membre));
    }

    public List<JuryDTO.Response> getAllMembres() {
        return membreJuryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<JuryDTO.Response> getMembresParSoutenance(String soutenanceId) {
        return membreJuryRepository.findBySoutenanceId(soutenanceId)
                .stream().map(this::toResponse).toList();
    }

    public void retirerMembre(String id) {
        if (!membreJuryRepository.existsById(id)) {
            throw new RuntimeException("Membre jury introuvable : " + id);
        }
        membreJuryRepository.deleteById(id);
    }

    public List<JuryDTO.Response> autoAffecterJury(String soutenanceId, String encadrantId) {
        if (membreJuryRepository.countBySoutenanceId(soutenanceId) > 0) {
            throw new IllegalArgumentException("Cette soutenance a déjà des membres de jury assignés");
        }

        List<EnseignantClientDTO> tous = utilisateurClient.getAllEnseignants();
        List<EnseignantClientDTO> disponibles = tous.stream()
                .filter(e -> !e.getId().toString().equals(encadrantId))
                .toList();

        if (disponibles.size() < 3) {
            throw new IllegalStateException("Pas assez d'enseignants disponibles pour former un jury (minimum 3 hors encadrant)");
        }

        List<RoleJury> roles = List.of(RoleJury.PRESIDENT, RoleJury.RAPPORTEUR, RoleJury.EXAMINATEUR);
        List<JuryDTO.Response> result = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            JuryDTO.Request req = new JuryDTO.Request(
                    disponibles.get(i).getId().toString(),
                    soutenanceId,
                    roles.get(i)
            );
            result.add(affecterMembre(req));
        }

        return result;
    }

    private JuryDTO.Response toResponse(MembreJury m) {
        return new JuryDTO.Response(m.getId(), m.getEnseignantId(), m.getSoutenanceId(), m.getRole());
    }
}
