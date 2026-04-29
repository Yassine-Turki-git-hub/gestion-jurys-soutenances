package com.soutenance.jury.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soutenance.jury.dto.NoteEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void publierNotes(String soutenanceId, List<Double> notes) {
        try {
            NoteEventDTO event = new NoteEventDTO(soutenanceId, notes);
            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend("notes.saisies", message);
            log.info("Notes publiées pour soutenanceId={} ({} notes)", soutenanceId, notes.size());
        } catch (JsonProcessingException e) {
            log.error("Échec publication notes pour soutenanceId={}", soutenanceId, e);
        }
    }
}
