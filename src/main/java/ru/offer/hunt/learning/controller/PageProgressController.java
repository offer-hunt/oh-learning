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
import ru.offer.hunt.learning.model.dto.PageProgressDto;
import ru.offer.hunt.learning.model.dto.PageProgressUpsertRequest;
import ru.offer.hunt.learning.service.PageProgressService;

@RestController
@RequestMapping("/api/learning/page-progress")
@RequiredArgsConstructor
@Validated
@Tag(name = "Page Progress", description = "Прогресс по страницам курса")
@SecurityRequirement(name = "bearerAuth")
public class PageProgressController {

  private final PageProgressService service;

  @GetMapping("/{userId}/{pageId}")
  @Operation(summary = "Получить прогресс по странице")
  public PageProgressDto get(@PathVariable UUID userId, @PathVariable UUID pageId) {
    return service.get(userId, pageId);
  }

  @GetMapping
  @Operation(summary = "Список прогрессов по страницам", description = "Фильтры: userId или pageId")
  public List<PageProgressDto> list(
      @RequestParam(required = false) UUID userId, @RequestParam(required = false) UUID pageId) {
    if (userId != null) {
      return service.listByUser(userId);
    }
    if (pageId != null) {
      return service.listByPage(pageId);
    }
    return List.of();
  }

  @PostMapping("/{userId}/{pageId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать прогресс по странице")
  public PageProgressDto create(
      @PathVariable UUID userId,
      @PathVariable UUID pageId,
      @RequestBody PageProgressUpsertRequest req) {
    return service.create(userId, pageId, req);
  }

  @PutMapping("/{userId}/{pageId}")
  @Operation(summary = "Обновить прогресс по странице")
  public PageProgressDto update(
      @PathVariable UUID userId,
      @PathVariable UUID pageId,
      @RequestBody PageProgressUpsertRequest req) {
    return service.update(userId, pageId, req);
  }

  @DeleteMapping("/{userId}/{pageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить прогресс по странице")
  public void delete(@PathVariable UUID userId, @PathVariable UUID pageId) {
    service.delete(userId, pageId);
  }
}
