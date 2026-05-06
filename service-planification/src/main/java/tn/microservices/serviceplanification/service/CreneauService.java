package tn.microservices.serviceplanification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.microservices.serviceplanification.dto.CreneauDTO;
import tn.microservices.serviceplanification.entity.Creneau;
import tn.microservices.serviceplanification.exception.ResourceNotFoundException;
import tn.microservices.serviceplanification.repository.CreneauRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreneauService {

    private final CreneauRepository creneauRepository;

    // CREATE
    public CreneauDTO create(CreneauDTO creneauDTO) {
        Creneau creneau = new Creneau();
        creneau.setDate(creneauDTO.getDate());
        creneau.setHeureDebut(creneauDTO.getHeureDebut());
        creneau.setHeureFin(creneauDTO.getHeureFin());
        Creneau saved = creneauRepository.save(creneau);
        return mapToDTO(saved);
    }

    // READ ALL
    public List<CreneauDTO> getAll() {
        return creneauRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public CreneauDTO getById(Long id) {
        Creneau creneau = creneauRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau avec l'ID " + id + " introuvable"));
        return mapToDTO(creneau);
    }

    // UPDATE
    public CreneauDTO update(Long id, CreneauDTO creneauDTO) {
        Creneau creneau = creneauRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau avec l'ID " + id + " introuvable"));

        if (creneauDTO.getDate() != null) {
            creneau.setDate(creneauDTO.getDate());
        }
        if (creneauDTO.getHeureDebut() != null) {
            creneau.setHeureDebut(creneauDTO.getHeureDebut());
        }
        if (creneauDTO.getHeureFin() != null) {
            creneau.setHeureFin(creneauDTO.getHeureFin());
        }

        Creneau updated = creneauRepository.save(creneau);
        return mapToDTO(updated);
    }

    // DELETE
    public void delete(Long id) {
        if (!creneauRepository.existsById(id)) {
            throw new ResourceNotFoundException("Créneau avec l'ID " + id + " introuvable");
        }
        creneauRepository.deleteById(id);
    }

    // HELPER METHOD
    private CreneauDTO mapToDTO(Creneau creneau) {
        return new CreneauDTO(
                creneau.getId(),
                creneau.getDate(),
                creneau.getHeureDebut(),
                creneau.getHeureFin()
        );
    }
}
