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
import ru.offer.hunt.learning.model.dto.LessonProgressDto;
import ru.offer.hunt.learning.model.dto.LessonProgressUpsertRequest;
import ru.offer.hunt.learning.service.LessonProgressService;

@RestController
@RequestMapping("/api/learning/lesson-progress")
@RequiredArgsConstructor
@Validated
public class LessonProgressController {

  private final LessonProgressService service;

  @GetMapping("/{userId}/{lessonId}")
  public LessonProgressDto get(@PathVariable UUID userId, @PathVariable UUID lessonId) {
    return service.get(userId, lessonId);
  }

  @GetMapping
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
  public LessonProgressDto create(
      @PathVariable UUID userId,
      @PathVariable UUID lessonId,
      @RequestBody LessonProgressUpsertRequest req) {
    return service.create(userId, lessonId, req);
  }

  @PutMapping("/{userId}/{lessonId}")
  public LessonProgressDto update(
      @PathVariable UUID userId,
      @PathVariable UUID lessonId,
      @RequestBody LessonProgressUpsertRequest req) {
    return service.update(userId, lessonId, req);
  }

  @DeleteMapping("/{userId}/{lessonId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID userId, @PathVariable UUID lessonId) {
    service.delete(userId, lessonId);
  }
}
