package ru.offer.hunt.learning.model.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningExportDto {
  private UUID userId;
  private OffsetDateTime generatedAt;

  private List<EnrollmentDto> enrollments;
  private List<CourseProgressDto> courseProgress;
  private List<LessonProgressDto> lessonProgress;
  private List<PageProgressDto> pageProgress;
  private List<QuestionStateDto> questionStates;
  private List<LearningRatingDto> ratings;
}
