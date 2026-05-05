package tn.microservices.serviceplanification.service;

import tn.microservices.serviceplanification.entity.Creneau;
import tn.microservices.serviceplanification.repository.CreneauRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreneauService {

    private final CreneauRepository creneauRepository;

    public Creneau create(Creneau creneau) {
        return creneauRepository.save(creneau);
    }

    public List<Creneau> getAll() {
        return creneauRepository.findAll();
    }
}