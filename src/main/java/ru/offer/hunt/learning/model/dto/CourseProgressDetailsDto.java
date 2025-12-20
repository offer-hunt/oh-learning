package ru.offer.hunt.learning.model.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;
import ru.offer.hunt.learning.model.enums.LessonProgressStatus;

@Getter
@Setter
public class CourseProgressDetailsDto {
  private UUID courseId;

  private EnrollmentStatus enrollmentStatus;
  private Integer courseProgressPercent;
  private LessonProgressStatus computedStatus;

  private OffsetDateTime enrolledAt;
  private OffsetDateTime completedAt;
  private OffsetDateTime lastActivityAt;

  private CourseDetailsStatsDto stats;
  private List<ChapterProgressDto> chapters;
}
