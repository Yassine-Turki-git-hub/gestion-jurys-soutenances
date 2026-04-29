package com.soutenance.jury.service;

import com.soutenance.jury.dto.NoteDTO;
import com.soutenance.jury.entity.MembreJury;
import com.soutenance.jury.entity.Note;
import com.soutenance.jury.messaging.NotePublisher;
import com.soutenance.jury.repository.MembreJuryRepository;
import com.soutenance.jury.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final MembreJuryRepository membreJuryRepository;
    private final NotePublisher notePublisher;

    @Transactional
    public NoteDTO.Response saisirNote(NoteDTO.Request request) {
        MembreJury membre = membreJuryRepository.findById(request.getMembreJuryId())
                .orElseThrow(() -> new RuntimeException("Membre jury introuvable : " + request.getMembreJuryId()));

        if (noteRepository.existsByMembreJury_Id(request.getMembreJuryId())) {
            throw new IllegalArgumentException("Ce membre a déjà saisi sa note pour cette soutenance");
        }

        if (request.getValeur() < 0 || request.getValeur() > 20) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 20");
        }

        Note note = Note.builder()
                .membreJury(membre)
                .soutenanceId(membre.getSoutenanceId())
                .valeur(request.getValeur())
                .commentaire(request.getCommentaire())
                .build();

        note = noteRepository.save(note);

        long totalMembres = membreJuryRepository.countBySoutenanceId(membre.getSoutenanceId());
        long totalNotes = noteRepository.countBySoutenanceId(membre.getSoutenanceId());

        if (totalNotes >= totalMembres && totalMembres > 0) {
            List<Double> notes = noteRepository.findBySoutenanceId(membre.getSoutenanceId())
                    .stream()
                    .map(n -> n.getValeur().doubleValue())
                    .toList();
            notePublisher.publierNotes(membre.getSoutenanceId(), notes);
        }

        return toResponse(note);
    }

    public List<NoteDTO.Response> getNotesParSoutenance(String soutenanceId) {
        return noteRepository.findBySoutenanceId(soutenanceId)
                .stream().map(this::toResponse).toList();
    }

    private NoteDTO.Response toResponse(Note n) {
        return new NoteDTO.Response(
                n.getId(),
                n.getMembreJury().getId(),
                n.getSoutenanceId(),
                n.getValeur(),
                n.getCommentaire(),
                n.getDateSaisie()
        );
    }
}
