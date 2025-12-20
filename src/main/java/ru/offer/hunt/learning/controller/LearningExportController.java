package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.offer.hunt.learning.model.dto.LearningExportDto;
import ru.offer.hunt.learning.security.SecurityUtils;
import ru.offer.hunt.learning.service.LearningExportService;

@RestController
@RequestMapping("/api/learning/export")
@RequiredArgsConstructor
@Tag(name = "Export", description = "Экспорт данных learning-подсистемы")
@SecurityRequirement(name = "bearerAuth")
public class LearningExportController {

  private final LearningExportService service;

  @GetMapping
  @Operation(
      summary = "Экспорт всех данных по обучению текущего пользователя",
      description =
          "Возвращает сводный объект с данными текущего пользователя в learning-подсистеме: "
              + "зачисления на курсы, прогресс по курсам/урокам/страницам, состояния вопросов и оценки.")
  public LearningExportDto export(Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.export(userId);
  }
}
