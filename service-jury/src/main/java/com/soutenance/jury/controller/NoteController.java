package com.soutenance.jury.controller;

import com.soutenance.jury.dto.NoteDTO;
import com.soutenance.jury.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jury/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteDTO.Response> saisir(@RequestBody NoteDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.saisirNote(request));
    }

    @GetMapping("/soutenance/{soutenanceId}")
    public ResponseEntity<List<NoteDTO.Response>> getNotes(@PathVariable String soutenanceId) {
        return ResponseEntity.ok(noteService.getNotesParSoutenance(soutenanceId));
    }
}
