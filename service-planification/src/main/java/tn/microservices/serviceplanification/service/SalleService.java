package tn.microservices.serviceplanification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.microservices.serviceplanification.dto.SalleDTO;
import tn.microservices.serviceplanification.entity.Salle;
import tn.microservices.serviceplanification.exception.ResourceNotFoundException;
import tn.microservices.serviceplanification.repository.SalleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalleService {

    private final SalleRepository salleRepository;

    // CREATE
    public SalleDTO create(SalleDTO salleDTO) {
        Salle salle = new Salle();
        salle.setNumero(salleDTO.getNumero());
        salle.setBatiment(salleDTO.getBatiment());
        salle.setCapacite(salleDTO.getCapacite());
        Salle saved = salleRepository.save(salle);
        return mapToDTO(saved);
    }

    // READ ALL
    public List<SalleDTO> getAll() {
        return salleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public SalleDTO getById(Long id) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle avec l'ID " + id + " introuvable"));
        return mapToDTO(salle);
    }

    // UPDATE
    public SalleDTO update(Long id, SalleDTO salleDTO) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle avec l'ID " + id + " introuvable"));

        if (salleDTO.getNumero() != null) {
            salle.setNumero(salleDTO.getNumero());
        }
        if (salleDTO.getBatiment() != null) {
            salle.setBatiment(salleDTO.getBatiment());
        }
        if (salleDTO.getCapacite() != null) {
            salle.setCapacite(salleDTO.getCapacite());
        }

        Salle updated = salleRepository.save(salle);
        return mapToDTO(updated);
    }

    // DELETE
    public void delete(Long id) {
        if (!salleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salle avec l'ID " + id + " introuvable");
        }
        salleRepository.deleteById(id);
    }

    // HELPER METHOD
    private SalleDTO mapToDTO(Salle salle) {
        return new SalleDTO(
                salle.getId(),
                salle.getNumero(),
                salle.getBatiment(),
                salle.getCapacite(),
                true // Par défaut, une salle est libre
        );
    }
}
