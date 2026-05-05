package tn.microservices.serviceplanification.controller;

import tn.microservices.serviceplanification.entity.Creneau;
import tn.microservices.serviceplanification.service.CreneauService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creneaux")
@RequiredArgsConstructor
public class CreneauController {

    private final CreneauService creneauService;

    @PostMapping
    public Creneau create(@RequestBody Creneau creneau) {
        return creneauService.create(creneau);
    }

    @GetMapping
    public List<Creneau> getAll() {
        return creneauService.getAll();
    }
}