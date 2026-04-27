package tn.microservices.serviceresultats.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.microservices.serviceresultats.dto.NoteEventDTO;
import tn.microservices.serviceresultats.service.ResultatService;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoteConsumer {

    private final ResultatService resultatService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "notes.saisies")
    public void consume(String message) {
        try {
            NoteEventDTO event = objectMapper.readValue(message, NoteEventDTO.class);
            if (event.getSoutenanceId() == null || event.getSoutenanceId().isBlank()) {
                log.warn("Received event with missing soutenanceId: {}", message);
                return;
            }
            if (event.getNotes() == null || event.getNotes().isEmpty()) {
                log.warn("Received event with empty notes for soutenanceId={}", event.getSoutenanceId());
                return;
            }
            resultatService.traiterNotes(event.getSoutenanceId(), event.getNotes());
            log.info("Processed notes for soutenanceId={} ({} notes)", event.getSoutenanceId(), event.getNotes().size());
        } catch (Exception e) {
            log.error("Failed to process message from notes.saisies: {}", message, e);
            // In production we might republish to a DLQ. For now, we log and swallow to avoid requeue storms.
        }
    }
}
