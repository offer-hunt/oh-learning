package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.offer.hunt.learning.model.dto.QuestionStateDto;
import ru.offer.hunt.learning.model.dto.QuestionStateUpsertRequest;
import ru.offer.hunt.learning.service.QuestionStateService;

@RestController
@RequestMapping("/api/learning/question-states")
@RequiredArgsConstructor
@Validated
@Tag(name = "Question States", description = "Состояния вопросов: решённость и последняя попытка")
@SecurityRequirement(name = "bearerAuth")
public class QuestionStateController {

  private final QuestionStateService service;

  @GetMapping("/{userId}/{questionId}")
  @Operation(summary = "Получить состояние вопроса")
  public QuestionStateDto get(@PathVariable UUID userId, @PathVariable UUID questionId) {
    return service.get(userId, questionId);
  }

  @GetMapping
  @Operation(summary = "Список состояний вопросов", description = "Фильтры: userId или questionId")
  public List<QuestionStateDto> list(
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) UUID questionId) {
    if (userId != null) {
      return service.listByUser(userId);
    }
    if (questionId != null) {
      return service.listByQuestion(questionId);
    }
    return List.of();
  }

  @PostMapping("/{userId}/{questionId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать состояние вопроса")
  public QuestionStateDto create(
      @PathVariable UUID userId,
      @PathVariable UUID questionId,
      @RequestBody QuestionStateUpsertRequest req) {
    return service.create(userId, questionId, req);
  }

  @PutMapping("/{userId}/{questionId}")
  @Operation(summary = "Обновить состояние вопроса")
  public QuestionStateDto update(
      @PathVariable UUID userId,
      @PathVariable UUID questionId,
      @RequestBody QuestionStateUpsertRequest req) {
    return service.update(userId, questionId, req);
  }

  @DeleteMapping("/{userId}/{questionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить состояние вопроса")
  public void delete(@PathVariable UUID userId, @PathVariable UUID questionId) {
    service.delete(userId, questionId);
  }
}
