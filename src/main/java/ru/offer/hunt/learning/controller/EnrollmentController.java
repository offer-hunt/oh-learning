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
import ru.offer.hunt.learning.model.dto.EnrollmentDto;
import ru.offer.hunt.learning.model.dto.EnrollmentUpsertRequest;
import ru.offer.hunt.learning.service.EnrollmentService;

@RestController
@RequestMapping("/api/learning/enrollments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Enrollments", description = "Управление зачислениями на курсы")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

  private final EnrollmentService service;

  @GetMapping("/{userId}/{courseId}")
  @Operation(
      summary = "Получить зачисление",
      description = "Возвращает статус зачисления пользователя на курс")
  public EnrollmentDto get(@PathVariable UUID userId, @PathVariable UUID courseId) {
    return service.get(userId, courseId);
  }

  @GetMapping
  @Operation(summary = "Список зачислений", description = "Фильтры: userId или courseId")
  public List<EnrollmentDto> list(
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
  @Operation(
      summary = "Создать/зачислить",
      description = "Создаёт запись зачисления для пользователя и курса")
  public EnrollmentDto create(
      @PathVariable UUID userId,
      @PathVariable UUID courseId,
      @RequestBody EnrollmentUpsertRequest req) {
    return service.create(userId, courseId, req);
  }

  @PutMapping("/{userId}/{courseId}")
  @Operation(
      summary = "Обновить зачисление",
      description = "Обновляет статус/источник и таймстемпы")
  public EnrollmentDto update(
      @PathVariable UUID userId,
      @PathVariable UUID courseId,
      @RequestBody EnrollmentUpsertRequest req) {
    return service.update(userId, courseId, req);
  }

  @DeleteMapping("/{userId}/{courseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить зачисление")
  public void delete(@PathVariable UUID userId, @PathVariable UUID courseId) {
    service.delete(userId, courseId);
  }
}
