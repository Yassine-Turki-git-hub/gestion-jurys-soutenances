package tn.microservices.serviceresultats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.microservices.serviceresultats.dto.ResultatDTO;
import tn.microservices.serviceresultats.service.ResultatService;

import java.util.List;

@RestController
@RequestMapping("/api/resultats")
@RequiredArgsConstructor
public class ResultatController {

    private final ResultatService resultatService;

    record RecalculRequest(String soutenanceId, List<Double> notes) {}

    @GetMapping
    public ResponseEntity<List<ResultatDTO>> getAll() {
        return ResponseEntity.ok(resultatService.getAll());
    }

    @GetMapping("/{soutenanceId}")
    public ResponseEntity<ResultatDTO> getBySoutenanceId(@PathVariable String soutenanceId) {
        return resultatService.getBySoutenanceId(soutenanceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/recalculer")
    public ResponseEntity<ResultatDTO> recalculer(@RequestBody RecalculRequest req) {
        resultatService.traiterNotes(req.soutenanceId(), req.notes());
        return resultatService.getBySoutenanceId(req.soutenanceId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().build());
    }
}
