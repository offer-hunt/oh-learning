package ru.offer.hunt.learning.controller;

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
public class QuestionStateController {

  private final QuestionStateService service;

  @GetMapping("/{userId}/{questionId}")
  public QuestionStateDto get(@PathVariable UUID userId, @PathVariable UUID questionId) {
    return service.get(userId, questionId);
  }

  @GetMapping
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
  public QuestionStateDto create(
      @PathVariable UUID userId,
      @PathVariable UUID questionId,
      @RequestBody QuestionStateUpsertRequest req) {
    return service.create(userId, questionId, req);
  }

  @PutMapping("/{userId}/{questionId}")
  public QuestionStateDto update(
      @PathVariable UUID userId,
      @PathVariable UUID questionId,
      @RequestBody QuestionStateUpsertRequest req) {
    return service.update(userId, questionId, req);
  }

  @DeleteMapping("/{userId}/{questionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID userId, @PathVariable UUID questionId) {
    service.delete(userId, questionId);
  }
}
