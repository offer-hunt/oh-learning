package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.offer.hunt.learning.model.dto.InternalUpsertResultRequest;
import ru.offer.hunt.learning.model.dto.QuestionStateDto;
import ru.offer.hunt.learning.service.QuestionStateService;

@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
@Validated
@Tag(name = "Internal", description = "Internal API for service-to-service интеграций")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class InternalResultController {

  private final QuestionStateService questionStateService;

  @PostMapping("/upsert-result")
  @Operation(
      summary = "Upsert результата проверки решения (из Evaluation)",
      description =
          "Evaluation сервис отправляет итог проверки; Learning обновляет QuestionState и пересчитывает прогресс.")
  public ResponseEntity<QuestionStateDto> upsertResult(
      @RequestBody @Valid InternalUpsertResultRequest req) {

    log.info(
        "Internal upsert-result: userId={}, questionId={}, status={}, score={}",
        req.getUserId(),
        req.getQuestionId(),
        req.getSubmissionStatus(),
        req.getScore());

    return ResponseEntity.ok(questionStateService.upsertResultFromEvaluation(req));
  }
}
