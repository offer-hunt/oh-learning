package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.offer.hunt.learning.client.AiIntegrationClient;
import ru.offer.hunt.learning.model.dto.*;
import ru.offer.hunt.learning.model.entity.AiTaskStats;
import ru.offer.hunt.learning.model.id.AiTaskStatsId;
import ru.offer.hunt.learning.model.repository.AiTaskStatsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAssistanceService {
  private final AiIntegrationClient aiClient;
  private final AiTaskStatsRepository aiStatsRepo;

  private static final int MAX_HINTS = 3;

  public AiResponseDto explainMaterial(UUID userId, ExplainRequestDto req) {
    if (!StringUtils.hasText(req.getUserQuery())) {
      log.warn(
          "{User ID: {}} requested AI explanation for {Materials ID: {}}. Status: Empty Request.",
          userId,
          req.getMaterialId());
      return AiResponseDto.builder()
          .success(false)
          .errorMessage("Пожалуйста, введите ваш вопрос")
          .build();
    }

    try {
      AiExternalExplainRequest aiReq =
          AiExternalExplainRequest.builder()
              .text(req.getSelectedText())
              .query(req.getUserQuery())
              .build();

      AiResponseDto response = aiClient.explainConcept(aiReq);

      log.info(
          "{User ID: {}} requested AI explanation for {Materials ID: {}}. Request: \"{}\". Status: Success.",
          userId,
          req.getMaterialId(),
          req.getUserQuery());

      return response;

    } catch (Exception e) {
      log.error(
          "{User ID: {}} requested AI explanation for {Materials ID: {}}. Status: AI Service Unavailable",
          userId,
          req.getMaterialId());
      return AiResponseDto.builder()
          .success(false)
          .errorMessage("В настоящее время AI-ассистент недоступен, попробуйте позже")
          .build();
    }
  }

  @Transactional
  public AiResponseDto getTaskHint(UUID userId, UUID taskId, HintRequestDto req) {
    AiTaskStatsId id = new AiTaskStatsId(userId, taskId);

    AiTaskStats stats =
        aiStatsRepo
            .findById(id)
            .orElseGet(
                () -> {
                  AiTaskStats newStats = new AiTaskStats();
                  newStats.setUserId(userId);
                  newStats.setTaskId(taskId);
                  newStats.setHintsUsed(0);
                  newStats.setLastRequestAt(OffsetDateTime.now());
                  return aiStatsRepo.save(newStats);
                });

    if (stats.getHintsUsed() >= MAX_HINTS) {
      log.warn(
          "{User ID: {}} requested AI hint for task {Task ID: {}}. Hints used: {}. Status: Limit Exceeded.",
          userId,
          taskId,
          MAX_HINTS);
      return AiResponseDto.builder()
          .success(false)
          .errorMessage("Вы исчерпали лимит подсказок для этой задачи")
          .build();
    }

    try {
      AiExternalHintRequest aiReq =
          AiExternalHintRequest.builder()
              .taskId(taskId)
              .code(req.getCurrentCode())
              .previousHintsCount(stats.getHintsUsed())
              .build();

      AiResponseDto response = aiClient.getHint(aiReq);

      if (response.isSuccess()) {
        stats.setHintsUsed(stats.getHintsUsed() + 1);
        stats.setLastRequestAt(OffsetDateTime.now());
        aiStatsRepo.save(stats);

        log.info(
            "{User ID: {}} requested AI hint for task {Task ID: {}}. Hints used: {}. Status: Success.",
            userId,
            taskId,
            stats.getHintsUsed());
      }

      return response;

    } catch (Exception e) {
      log.error(
          "{User ID: {}} requested AI hint for task {Task ID: {}}. Status: AI Service Unavailable.",
          userId,
          taskId);
      return AiResponseDto.builder()
          .success(false)
          .errorMessage("В настоящее время ассистент недоступен, попробуйте позже")
          .build();
    }
  }
}
