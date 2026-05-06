package tn.microservices.serviceplanification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.microservices.serviceplanification.dto.SalleDTO;
import tn.microservices.serviceplanification.service.SalleService;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
public class SalleController {

    private final SalleService salleService;

    /**
     * CREATE - Create a new room
     * @param salleDTO The room data
     * @return The created room with status 201
     */
    @PostMapping
    public ResponseEntity<SalleDTO> create(@RequestBody SalleDTO salleDTO) {
        SalleDTO created = salleService.create(salleDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * READ ALL - Get all rooms
     * @return List of all rooms
     */
    @GetMapping
    public ResponseEntity<List<SalleDTO>> getAll() {
        List<SalleDTO> salles = salleService.getAll();
        return new ResponseEntity<>(salles, HttpStatus.OK);
    }

    /**
     * READ BY ID - Get a specific room by ID
     * @param id The room ID
     * @return The room with status 200
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalleDTO> getById(@PathVariable Long id) {
        SalleDTO salle = salleService.getById(id);
        return new ResponseEntity<>(salle, HttpStatus.OK);
    }

    /**
     * UPDATE - Update an existing room
     * @param id The room ID to update
     * @param salleDTO The updated room data
     * @return The updated room
     */
    @PutMapping("/{id}")
    public ResponseEntity<SalleDTO> update(@PathVariable Long id, @RequestBody SalleDTO salleDTO) {
        SalleDTO updated = salleService.update(id, salleDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    /**
     * DELETE - Delete a room
     * @param id The room ID to delete
     * @return Status 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        salleService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * CHECK AVAILABILITY - Check if a room is available
     * @param id The room ID
     * @return true if available, false otherwise
     */
    @GetMapping("/{id}/disponible")
    public ResponseEntity<Boolean> isAvailable(@PathVariable Long id) {
        try {
            salleService.getById(id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }
}
