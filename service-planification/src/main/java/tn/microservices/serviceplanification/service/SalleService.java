package tn.microservices.serviceplanification.service;

import tn.microservices.serviceplanification.entity.Salle;
import tn.microservices.serviceplanification.repository.SalleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalleService {

    private final SalleRepository salleRepository;

    public Salle create(Salle salle) {
        return salleRepository.save(salle);
    }

    public List<Salle> getAll() {
        return salleRepository.findAll();
    }
}