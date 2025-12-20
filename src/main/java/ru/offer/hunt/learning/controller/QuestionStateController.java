package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.offer.hunt.learning.model.dto.QuestionStateDto;
import ru.offer.hunt.learning.model.dto.QuestionStateUpsertRequest;
import ru.offer.hunt.learning.security.SecurityUtils;
import ru.offer.hunt.learning.service.QuestionStateService;

@RestController
@RequestMapping("/api/learning/question-states")
@RequiredArgsConstructor
@Validated
@Tag(name = "Question States", description = "Состояния вопросов: решённость и последняя попытка")
@SecurityRequirement(name = "bearerAuth")
public class QuestionStateController {

  private final QuestionStateService service;

  @GetMapping("/{questionId}")
  @Operation(
      summary = "Получить состояние вопроса",
      description =
          "Возвращает состояние конкретного вопроса для текущего пользователя: "
              + "решён ли вопрос, время решения, последняя отправка и её статус.")
  public QuestionStateDto get(@PathVariable UUID questionId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.get(userId, questionId);
  }

  @GetMapping
  @Operation(
      summary = "Список состояний вопросов",
      description =
          "Если указан questionId — возвращает состояния всех пользователей по этому вопросу "
              + "(для аналитики/проверки). "
              + "Если questionId не указан — возвращает состояния всех вопросов текущего пользователя.")
  public List<QuestionStateDto> list(
      @RequestParam(required = false) UUID questionId, Authentication authentication) {
    if (questionId != null) {
      return service.listByQuestion(questionId);
    }
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.listByUser(userId);
  }

  @PostMapping("/{questionId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Создать состояние вопроса",
      description =
          "Создаёт состояние вопроса для текущего пользователя. "
              + "Используется при первой попытке решения вопроса.")
  public QuestionStateDto create(
      @PathVariable UUID questionId,
      @RequestBody QuestionStateUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.create(userId, questionId, req);
  }

  @PutMapping("/{questionId}")
  @Operation(
      summary = "Обновить состояние вопроса",
      description =
          "Обновляет состояние вопроса для текущего пользователя: "
              + "флаг решённости, время решения, последнюю попытку и статус проверки.")
  public QuestionStateDto update(
      @PathVariable UUID questionId,
      @RequestBody QuestionStateUpsertRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return service.update(userId, questionId, req);
  }

  @DeleteMapping("/{questionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Удалить состояние вопроса",
      description =
          "Удаляет состояние вопроса для текущего пользователя. "
              + "Обычно используется для сброса прогресса по вопросу.")
  public void delete(@PathVariable UUID questionId, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    service.delete(userId, questionId);
  }
}
