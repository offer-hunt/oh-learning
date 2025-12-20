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
import ru.offer.hunt.learning.model.dto.EnrollmentDto;
import ru.offer.hunt.learning.model.dto.EnrollmentUpsertRequest;
import ru.offer.hunt.learning.security.SecurityUtils;
import ru.offer.hunt.learning.service.EnrollmentService;

@RestController
@RequestMapping("/api/learning/enrollments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Enrollments", description = "Управление зачислениями на курсы")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

  private final EnrollmentService enrollmentService;

  @GetMapping("/{courseId}")
  @Operation(
      summary = "Моё зачисление на курс",
      description = "Возвращает запись о зачислении текущего пользователя на указанный курс.")
  public EnrollmentDto get(@PathVariable UUID courseId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return enrollmentService.get(userId, courseId);
  }

  @GetMapping
  @Operation(
      summary = "Список зачислений",
      description =
          "Если указан courseId — возвращает всех пользователей, зачисленных на курс. "
              + "Если courseId не указан — возвращает все зачисления текущего пользователя.")
  public List<EnrollmentDto> list(
      @RequestParam(required = false) UUID courseId, Authentication authentication) {
    if (courseId != null) {
      return enrollmentService.listByCourse(courseId);
    }
    UUID userId = SecurityUtils.getUserId(authentication);
    return enrollmentService.listByUser(userId);
  }

  @PostMapping("/{courseId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Зачислиться на курс",
      description =
          "Создаёт запись о зачислении текущего пользователя на курс. "
              + "Если зачисление уже существует, вернёт ошибку CONFLICT.")
  public EnrollmentDto create(
      @PathVariable UUID courseId,
      @RequestBody EnrollmentUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return enrollmentService.create(userId, courseId, req);
  }

  @PutMapping("/{courseId}")
  @Operation(
      summary = "Обновить моё зачисление",
      description =
          "Обновляет статус зачисления текущего пользователя на курс "
              + "(например, пометить COMPLETED или изменить source).")
  public EnrollmentDto update(
      @PathVariable UUID courseId,
      @RequestBody EnrollmentUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return enrollmentService.update(userId, courseId, req);
  }

  @DeleteMapping("/{courseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Отписаться от курса",
      description =
          "Логически отзывает зачисление текущего пользователя на курс: "
              + "ставит статус REVOKED и фиксирует дату отзыва. Идемпотентно.")
  public void unsubscribe(@PathVariable UUID courseId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    enrollmentService.revoke(userId, courseId);
  }
}
