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
import ru.offer.hunt.learning.model.dto.LearningRatingDto;
import ru.offer.hunt.learning.model.dto.LearningRatingUpsertRequest;
import ru.offer.hunt.learning.service.LearningRatingService;

@RestController
@RequestMapping("/api/learning/ratings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Ratings", description = "Оценки курса пользователями")
@SecurityRequirement(name = "bearerAuth")
public class LearningRatingController {

  private final LearningRatingService service;

  @GetMapping("/{userId}/{courseId}")
  @Operation(summary = "Получить оценку курса пользователем")
  public LearningRatingDto get(@PathVariable UUID userId, @PathVariable UUID courseId) {
    return service.get(userId, courseId);
  }

  @GetMapping
  @Operation(summary = "Список оценок", description = "Фильтры: userId или courseId")
  public List<LearningRatingDto> list(
      @RequestParam(required = false) UUID userId, @RequestParam(required = false) UUID courseId) {
    if (userId != null) {
      return service.listByUser(userId);
    }
    if (courseId != null) {
      return service.listByCourse(courseId);
    }
    return List.of();
  }

  @PostMapping("/{userId}/{courseId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать/поставить оценку")
  public LearningRatingDto create(
      @PathVariable UUID userId,
      @PathVariable UUID courseId,
      @RequestBody LearningRatingUpsertRequest req) {
    return service.create(userId, courseId, req);
  }

  @PutMapping("/{userId}/{courseId}")
  @Operation(summary = "Обновить оценку")
  public LearningRatingDto update(
      @PathVariable UUID userId,
      @PathVariable UUID courseId,
      @RequestBody LearningRatingUpsertRequest req) {
    return service.update(userId, courseId, req);
  }

  @DeleteMapping("/{userId}/{courseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить оценку")
  public void delete(@PathVariable UUID userId, @PathVariable UUID courseId) {
    service.delete(userId, courseId);
  }
}
