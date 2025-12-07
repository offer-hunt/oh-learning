package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
import ru.offer.hunt.learning.security.SecurityUtils;
import ru.offer.hunt.learning.service.LearningRatingService;

@RestController
@RequestMapping("/api/learning/ratings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Ratings", description = "Оценки курса пользователями")
@SecurityRequirement(name = "bearerAuth")
public class LearningRatingController {

  private final LearningRatingService service;

  @GetMapping("/{courseId}")
  @Operation(summary = "Получить оценку курса (текущий пользователь)")
  public LearningRatingDto get(@PathVariable UUID courseId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.get(userId, courseId);
  }

  @GetMapping
  @Operation(
      summary = "Список оценок",
      description = "По умолчанию — для текущего пользователя. Если указан courseId — по курсу.")
  public List<LearningRatingDto> list(
      @RequestParam(required = false) UUID courseId, Authentication authentication) {
    if (courseId != null) {
      return service.listByCourse(courseId);
    }
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.listByUser(userId);
  }

  @PostMapping("/{courseId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать/поставить оценку (текущий пользователь)")
  public LearningRatingDto create(
      @PathVariable UUID courseId,
      @RequestBody LearningRatingUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.create(userId, courseId, req);
  }

  @PutMapping("/{courseId}")
  @Operation(summary = "Обновить оценку (текущий пользователь)")
  public LearningRatingDto update(
      @PathVariable UUID courseId,
      @RequestBody LearningRatingUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.update(userId, courseId, req);
  }

  @DeleteMapping("/{courseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить оценку (текущий пользователь)")
  public void delete(@PathVariable UUID courseId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    service.delete(userId, courseId);
  }
}
