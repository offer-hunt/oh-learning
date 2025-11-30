package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.offer.hunt.learning.model.dto.AiResponseDto;
import ru.offer.hunt.learning.model.dto.ExplainRequestDto;
import ru.offer.hunt.learning.model.dto.HintRequestDto;
import ru.offer.hunt.learning.service.AiAssistanceService;

import java.util.UUID;

@RestController
@RequestMapping("/api/learning/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistance", description = "API для взаимодействия с AI-ассистентом: объяснение материалов и подсказки по задачам")
public class AiAssistanceController {

    private final AiAssistanceService aiService;

    @Operation(
            summary = "Объяснение материала",
            description = "Позволяет студенту получить AI-объяснение выделенного фрагмента текста методички."
    )
    @PostMapping("/explain")
    public ResponseEntity<AiResponseDto> explainMaterial(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody ExplainRequestDto request
    ) {
        return ResponseEntity.ok(aiService.explainMaterial(userId, request));
    }

    @Operation(
            summary = "Подсказка по задаче",
            description = "Генерирует подсказку по коду задачи. Проверяет лимиты использования подсказок (макс 3)."
    )
    @PostMapping("/tasks/{taskId}/hint")
    public ResponseEntity<AiResponseDto> getTaskHint(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID taskId,
            @RequestBody HintRequestDto request
    ) {
        return ResponseEntity.ok(aiService.getTaskHint(userId, taskId, request));
    }
}