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
import ru.offer.hunt.learning.model.dto.CourseProgressDto;
import ru.offer.hunt.learning.model.dto.CourseProgressUpsertRequest;
import ru.offer.hunt.learning.security.SecurityUtils;
import ru.offer.hunt.learning.service.CourseProgressService;

@RestController
@RequestMapping("/api/learning/course-progress")
@RequiredArgsConstructor
@Validated
@Tag(name = "Course Progress", description = "Агрегированный прогресс по курсу")
@SecurityRequirement(name = "bearerAuth")
public class CourseProgressController {

  private final CourseProgressService service;

  @GetMapping("/{courseId}")
  @Operation(
      summary = "Получить прогресс курса",
      description =
          "Возвращает агрегированный прогресс текущего пользователя по курсу: "
              + "процент, статус (строкой) и временные метки вычисления/завершения.")
  public CourseProgressDto get(@PathVariable UUID courseId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.get(userId, courseId);
  }

  @GetMapping
  @Operation(
      summary = "Список прогрессов по курсам",
      description =
          "Если указан courseId — возвращает прогресс всех пользователей по курсу. "
              + "Если courseId не указан — возвращает прогресс текущего пользователя по всем его курсам.")
  public List<CourseProgressDto> list(
      @RequestParam(required = false) UUID courseId, Authentication authentication) {
    if (courseId != null) {
      return service.listByCourse(courseId);
    }
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.listByUser(userId);
  }

  @PostMapping("/{courseId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Создать прогресс курса",
      description =
          "Создаёт запись агрегированного прогресса по курсу для текущего пользователя. "
              + "Обычно вызывается фоновым воркером после пересчёта прогресса.")
  public CourseProgressDto create(
      @PathVariable UUID courseId,
      @RequestBody CourseProgressUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.create(userId, courseId, req);
  }

  @PutMapping("/{courseId}")
  @Operation(
      summary = "Обновить прогресс курса",
      description =
          "Обновляет агрегированный прогресс по курсу для текущего пользователя. "
              + "Используется фоновыми задачами/воркерами при пересчёте прогресса.")
  public CourseProgressDto update(
      @PathVariable UUID courseId,
      @RequestBody CourseProgressUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.update(userId, courseId, req);
  }

  @DeleteMapping("/{courseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Удалить прогресс курса",
      description = "Удаляет запись агрегированного прогресса по курсу для текущего пользователя.")
  public void delete(@PathVariable UUID courseId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    service.delete(userId, courseId);
  }
}
