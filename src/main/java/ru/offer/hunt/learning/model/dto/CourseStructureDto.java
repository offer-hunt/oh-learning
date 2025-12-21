package ru.offer.hunt.learning.model.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CourseStructureDto(
    UUID courseId,
    String title,
    Integer version,
    String status,
    OffsetDateTime updatedAt,
    List<LessonDto> lessons) {
  public record LessonDto(
      UUID lessonId,
      String title,
      String description,
      Integer orderIndex,
      Integer durationMin,
      Boolean isDemo,
      List<PageDto> pages) {}

  public record PageDto(
      UUID pageId,
      String title,
      String pageType,
      Integer sortOrder,
      MethodicalContentDto methodicalContent,
      List<QuestionDto> questions) {}

  public record MethodicalContentDto(
      String markdown, String externalVideoUrl, OffsetDateTime updatedAt) {}

  public record QuestionDto(
      UUID questionId,
      String type,
      String text,
      String correctAnswer,
      Boolean useAiCheck,
      Integer points,
      Integer sortOrder,
      List<QuestionOptionDto> options,
      List<QuestionTestCaseDto> testCases) {}

  public record QuestionOptionDto(
      UUID optionId, String label, Boolean isCorrect, Integer sortOrder) {}

  public record QuestionTestCaseDto(
      UUID testCaseId,
      String inputData,
      String expectedOutput,
      Integer timeoutMs,
      Integer memoryLimitMb) {}
}
