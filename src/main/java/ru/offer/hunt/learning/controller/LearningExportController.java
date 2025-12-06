package ru.offer.hunt.learning.controller;

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
  public LearningExportDto export(Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.export(userId);
  }
}
