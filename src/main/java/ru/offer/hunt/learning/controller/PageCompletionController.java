package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.offer.hunt.learning.model.dto.PageCompleteRequest;
import ru.offer.hunt.learning.model.dto.PageProgressDto;
import ru.offer.hunt.learning.security.SecurityUtils;
import ru.offer.hunt.learning.service.PageProgressService;

@RestController
@RequestMapping("/api/v1/pages")
@RequiredArgsConstructor
@Validated
@Tag(name = "Pages", description = "Операции над страницами в learning (прогресс)")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class PageCompletionController {

  private final PageProgressService pageProgressService;

  @PostMapping("/{pageId}/complete")
  @Operation(
      summary = "Отметить страницу как пройденную",
      description =
          "Ставит PageProgress=COMPLETED и пересчитывает LessonProgress и CourseProgress. "
              + "Learning сам берёт структуру курса из Course Service.")
  public PageProgressDto complete(
      @PathVariable UUID pageId, @RequestBody @Valid PageCompleteRequest req, Authentication auth) {

    UUID userId = SecurityUtils.getUserId(auth);

    log.info(
        "Complete page requested: userId={}, courseId={}, pageId={}",
        userId,
        req.getCourseId(),
        pageId);

    return pageProgressService.completePage(userId, pageId, req);
  }
}
