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
import ru.offer.hunt.learning.model.dto.LessonProgressDto;
import ru.offer.hunt.learning.model.dto.LessonProgressUpsertRequest;
import ru.offer.hunt.learning.security.SecurityUtils;
import ru.offer.hunt.learning.service.LessonProgressService;

@RestController
@RequestMapping("/api/learning/lesson-progress")
@RequiredArgsConstructor
@Validated
@Tag(name = "Lesson Progress", description = "Агрегированный прогресс по урокам")
@SecurityRequirement(name = "bearerAuth")
public class LessonProgressController {

  private final LessonProgressService service;

  @GetMapping("/{lessonId}")
  @Operation(summary = "Получить прогресс урока (текущий пользователь)")
  public LessonProgressDto get(@PathVariable UUID lessonId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.get(userId, lessonId);
  }

  @GetMapping
  @Operation(
      summary = "Список прогрессов уроков",
      description = "По умолчанию — для текущего пользователя. Если указан lessonId — по уроку.")
  public List<LessonProgressDto> list(
      @RequestParam(required = false) UUID lessonId, Authentication authentication) {
    if (lessonId != null) {
      return service.listByLesson(lessonId);
    }
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.listByUser(userId);
  }

  @PostMapping("/{lessonId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать прогресс урока (текущий пользователь)")
  public LessonProgressDto create(
      @PathVariable UUID lessonId,
      @RequestBody LessonProgressUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.create(userId, lessonId, req);
  }

  @PutMapping("/{lessonId}")
  @Operation(summary = "Обновить прогресс урока (текущий пользователь)")
  public LessonProgressDto update(
      @PathVariable UUID lessonId,
      @RequestBody LessonProgressUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.update(userId, lessonId, req);
  }

  @DeleteMapping("/{lessonId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить прогресс урока (текущий пользователь)")
  public void delete(@PathVariable UUID lessonId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    service.delete(userId, lessonId);
  }
}
