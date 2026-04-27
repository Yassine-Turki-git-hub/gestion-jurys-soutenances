package tn.microservices.serviceresultats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.microservices.serviceresultats.dto.ResultatDTO;
import tn.microservices.serviceresultats.entity.Resultat;
import tn.microservices.serviceresultats.repository.ResultatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResultatService {

    private final ResultatRepository resultatRepository;

    @Transactional
    public Resultat traiterNotes(String soutenanceId, List<Double> notes) {
        double moyenne = notes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        String mention = calculerMention(moyenne);

        // Upsert by soutenanceId to ensure idempotency
        Resultat resultat = resultatRepository.findBySoutenanceId(soutenanceId)
                .orElseGet(Resultat::new);

        resultat.setSoutenanceId(soutenanceId);
        resultat.setMoyenne(moyenne);
        resultat.setMention(mention);
        resultat.setValide(moyenne >= 10);
        resultat.setDateCalcul(LocalDateTime.now());

        return resultatRepository.save(resultat);
    }

    public Optional<ResultatDTO> getBySoutenanceId(String soutenanceId) {
        return resultatRepository.findBySoutenanceId(soutenanceId)
                .map(this::toDto);
    }

    private String calculerMention(double moyenne) {
        if (moyenne >= 16) return "Très Bien";
        if (moyenne >= 14) return "Bien";
        if (moyenne >= 12) return "Assez Bien";
        if (moyenne >= 10) return "Passable";
        return "Ajourné";
    }

    private ResultatDTO toDto(Resultat resultat) {
        return new ResultatDTO(
                resultat.getSoutenanceId(),
                resultat.getMoyenne(),
                resultat.getMention(),
                resultat.getValide(),
                resultat.getDateCalcul()
        );
    }
}
