package tn.microservices.serviceplanification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.microservices.serviceplanification.dto.CreneauDTO;
import tn.microservices.serviceplanification.service.CreneauService;

import java.util.List;

@RestController
@RequestMapping("/api/creneaux")
@RequiredArgsConstructor
public class CreneauController {

    private final CreneauService creneauService;

    /**
     * CREATE - Create a new time slot
     * @param creneauDTO The time slot data
     * @return The created time slot with status 201
     */
    @PostMapping
    public ResponseEntity<CreneauDTO> create(@RequestBody CreneauDTO creneauDTO) {
        CreneauDTO created = creneauService.create(creneauDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * READ ALL - Get all time slots
     * @return List of all time slots
     */
    @GetMapping
    public ResponseEntity<List<CreneauDTO>> getAll() {
        List<CreneauDTO> creneaux = creneauService.getAll();
        return new ResponseEntity<>(creneaux, HttpStatus.OK);
    }

    /**
     * READ BY ID - Get a specific time slot by ID
     * @param id The time slot ID
     * @return The time slot with status 200
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreneauDTO> getById(@PathVariable Long id) {
        CreneauDTO creneau = creneauService.getById(id);
        return new ResponseEntity<>(creneau, HttpStatus.OK);
    }

    /**
     * UPDATE - Update an existing time slot
     * @param id The time slot ID to update
     * @param creneauDTO The updated time slot data
     * @return The updated time slot
     */
    @PutMapping("/{id}")
    public ResponseEntity<CreneauDTO> update(@PathVariable Long id, @RequestBody CreneauDTO creneauDTO) {
        CreneauDTO updated = creneauService.update(id, creneauDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    /**
     * DELETE - Delete a time slot
     * @param id The time slot ID to delete
     * @return Status 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        creneauService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * CHECK AVAILABILITY - Check if a time slot is available
     * @param id The time slot ID
     * @return true if available, false otherwise
     */
    @GetMapping("/{id}/disponible")
    public ResponseEntity<Boolean> isAvailable(@PathVariable Long id) {
        try {
            creneauService.getById(id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }
}
