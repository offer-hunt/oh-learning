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
import ru.offer.hunt.learning.model.dto.CourseProgressDto;
import ru.offer.hunt.learning.model.dto.CourseProgressUpsertRequest;
import ru.offer.hunt.learning.service.CourseProgressService;

@RestController
@RequestMapping("/api/learning/course-progress")
@RequiredArgsConstructor
@Validated
@Tag(name = "Course Progress", description = "Агрегированный прогресс по курсу")
@SecurityRequirement(name = "bearerAuth")
public class CourseProgressController {

  private final CourseProgressService service;

  @GetMapping("/{userId}/{courseId}")
  @Operation(summary = "Получить прогресс курса", description = "Прогресс пользователя по курсу")
  public CourseProgressDto get(@PathVariable UUID userId, @PathVariable UUID courseId) {
    return service.get(userId, courseId);
  }

  @GetMapping
  @Operation(summary = "Список прогрессов курса", description = "Фильтры: userId или courseId")
  public List<CourseProgressDto> list(
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
  @Operation(summary = "Создать прогресс курса")
  public CourseProgressDto create(
      @PathVariable UUID userId,
      @PathVariable UUID courseId,
      @RequestBody CourseProgressUpsertRequest req) {
    return service.create(userId, courseId, req);
  }

  @PutMapping("/{userId}/{courseId}")
  @Operation(summary = "Обновить прогресс курса")
  public CourseProgressDto update(
      @PathVariable UUID userId,
      @PathVariable UUID courseId,
      @RequestBody CourseProgressUpsertRequest req) {
    return service.update(userId, courseId, req);
  }

  @DeleteMapping("/{userId}/{courseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить прогресс курса")
  public void delete(@PathVariable UUID userId, @PathVariable UUID courseId) {
    service.delete(userId, courseId);
  }
}
