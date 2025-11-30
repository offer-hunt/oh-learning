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
import ru.offer.hunt.learning.model.dto.LessonProgressDto;
import ru.offer.hunt.learning.model.dto.LessonProgressUpsertRequest;
import ru.offer.hunt.learning.service.LessonProgressService;

@RestController
@RequestMapping("/api/learning/lesson-progress")
@RequiredArgsConstructor
@Validated
@Tag(name = "Lesson Progress", description = "Агрегированный прогресс по урокам")
@SecurityRequirement(name = "bearerAuth")
public class LessonProgressController {

  private final LessonProgressService service;

  @GetMapping("/{userId}/{lessonId}")
  @Operation(summary = "Получить прогресс урока")
  public LessonProgressDto get(@PathVariable UUID userId, @PathVariable UUID lessonId) {
    return service.get(userId, lessonId);
  }

  @GetMapping
  @Operation(summary = "Список прогрессов уроков", description = "Фильтры: userId или lessonId")
  public List<LessonProgressDto> list(
      @RequestParam(required = false) UUID userId, @RequestParam(required = false) UUID lessonId) {
    if (userId != null) {
      return service.listByUser(userId);
    }
    if (lessonId != null) {
      return service.listByLesson(lessonId);
    }
    return List.of();
  }

  @PostMapping("/{userId}/{lessonId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать прогресс урока")
  public LessonProgressDto create(
      @PathVariable UUID userId,
      @PathVariable UUID lessonId,
      @RequestBody LessonProgressUpsertRequest req) {
    return service.create(userId, lessonId, req);
  }

  @PutMapping("/{userId}/{lessonId}")
  @Operation(summary = "Обновить прогресс урока")
  public LessonProgressDto update(
      @PathVariable UUID userId,
      @PathVariable UUID lessonId,
      @RequestBody LessonProgressUpsertRequest req) {
    return service.update(userId, lessonId, req);
  }

  @DeleteMapping("/{userId}/{lessonId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить прогресс урока")
  public void delete(@PathVariable UUID userId, @PathVariable UUID lessonId) {
    service.delete(userId, lessonId);
  }
}
