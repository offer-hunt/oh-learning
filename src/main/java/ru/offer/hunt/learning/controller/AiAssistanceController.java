package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.offer.hunt.learning.model.dto.AiResponseDto;
import ru.offer.hunt.learning.model.dto.ExplainRequestDto;
import ru.offer.hunt.learning.model.dto.HintRequestDto;
import ru.offer.hunt.learning.service.AiAssistanceService;

@RestController
@RequestMapping("/api/learning/ai")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "AI Assistance",
    description =
        "API для взаимодействия с AI-ассистентом: объяснение материалов и подсказки по задачам")
public class AiAssistanceController {

  private final AiAssistanceService aiService;

  @Operation(
      summary = "Объяснение материала",
      description =
          "Позволяет студенту получить AI-объяснение выделенного фрагмента текста методички.")
  @PostMapping("/explain")
  public ResponseEntity<AiResponseDto> explainMaterial(
      @RequestHeader UUID userId, @RequestBody ExplainRequestDto request) {
    return ResponseEntity.ok(aiService.explainMaterial(userId, request));
  }

  @Operation(
      summary = "Подсказка по задаче",
      description =
          "Генерирует подсказку по коду задачи. Проверяет лимиты использования подсказок (макс 3).")
  @PostMapping("/tasks/{taskId}/hint")
  public ResponseEntity<AiResponseDto> getTaskHint(
      @RequestHeader UUID userId,
      @PathVariable UUID taskId,
      @RequestBody HintRequestDto request) {
    return ResponseEntity.ok(aiService.getTaskHint(userId, taskId, request));
  }
}
