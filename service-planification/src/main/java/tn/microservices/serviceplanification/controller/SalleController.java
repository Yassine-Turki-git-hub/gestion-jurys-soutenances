package tn.microservices.serviceplanification.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.microservices.serviceplanification.entity.Salle;
import tn.microservices.serviceplanification.service.SalleService;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
public class SalleController {

    private final SalleService salleService;

    @PostMapping
    public Salle create(@RequestBody Salle salle) {
        return salleService.create(salle);
    }

    @GetMapping
    public List<Salle> getAll() {
        return salleService.getAll();
    }
}