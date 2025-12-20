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
import ru.offer.hunt.learning.model.dto.PageProgressDto;
import ru.offer.hunt.learning.model.dto.PageProgressUpsertRequest;
import ru.offer.hunt.learning.security.SecurityUtils;
import ru.offer.hunt.learning.service.PageProgressService;

@RestController
@RequestMapping("/api/learning/page-progress")
@RequiredArgsConstructor
@Validated
@Tag(name = "Page Progress", description = "Прогресс по страницам курса")
@SecurityRequirement(name = "bearerAuth")
public class PageProgressController {

  private final PageProgressService service;

  @GetMapping("/{pageId}")
  @Operation(
      summary = "Получить прогресс по странице",
      description =
          "Возвращает прогресс текущего пользователя по указанной странице: "
              + "статус (NOT_STARTED/IN_PROGRESS/COMPLETED), время просмотра и количество попыток.")
  public PageProgressDto get(@PathVariable UUID pageId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.get(userId, pageId);
  }

  @GetMapping
  @Operation(
      summary = "Список прогрессов по страницам",
      description =
          "Если указан pageId — возвращает прогресс всех пользователей по этой странице. "
              + "Если pageId не указан — возвращает прогресс текущего пользователя по всем его страницам.")
  public List<PageProgressDto> list(
      @RequestParam(required = false) UUID pageId, Authentication authentication) {
    if (pageId != null) {
      return service.listByPage(pageId);
    }
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.listByUser(userId);
  }

  @PostMapping("/{pageId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Создать прогресс по странице",
      description =
          "Создаёт запись прогресса по странице для текущего пользователя. "
              + "Используется, когда пользователь впервые взаимодействует со страницей.")
  public PageProgressDto create(
      @PathVariable UUID pageId,
      @RequestBody PageProgressUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.create(userId, pageId, req);
  }

  @PutMapping("/{pageId}")
  @Operation(
      summary = "Обновить прогресс по странице",
      description =
          "Обновляет прогресс по странице для текущего пользователя: "
              + "статус, время, количество попыток, версию контента.")
  public PageProgressDto update(
      @PathVariable UUID pageId,
      @RequestBody PageProgressUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.update(userId, pageId, req);
  }

  @DeleteMapping("/{pageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Удалить прогресс по странице",
      description = "Удаляет запись прогресса по странице для текущего пользователя.")
  public void delete(@PathVariable UUID pageId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    service.delete(userId, pageId);
  }
}
