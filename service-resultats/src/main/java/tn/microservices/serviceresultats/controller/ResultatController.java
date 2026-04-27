package tn.microservices.serviceresultats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.microservices.serviceresultats.dto.ResultatDTO;
import tn.microservices.serviceresultats.service.ResultatService;

@RestController
@RequestMapping("/api/resultats")
@RequiredArgsConstructor
public class ResultatController {

    private final ResultatService resultatService;

    @GetMapping("/{soutenanceId}")
    public ResponseEntity<ResultatDTO> getBySoutenanceId(@PathVariable String soutenanceId) {
        return resultatService.getBySoutenanceId(soutenanceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
