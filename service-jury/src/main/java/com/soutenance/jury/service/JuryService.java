package com.soutenance.jury.service;

import com.soutenance.jury.client.UtilisateurClient;
import com.soutenance.jury.dto.JuryDTO;
import com.soutenance.jury.entity.MembreJury;
import com.soutenance.jury.repository.MembreJuryRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private JuryDTO.Response toResponse(MembreJury m) {
        return new JuryDTO.Response(m.getId(), m.getEnseignantId(), m.getSoutenanceId(), m.getRole());
    }
}
