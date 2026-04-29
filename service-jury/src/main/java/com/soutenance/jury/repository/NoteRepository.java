package com.soutenance.jury.repository;

import com.soutenance.jury.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, String> {
    List<Note> findBySoutenanceId(String soutenanceId);
    long countBySoutenanceId(String soutenanceId);
    boolean existsByMembreJury_Id(String membreJuryId);
}
