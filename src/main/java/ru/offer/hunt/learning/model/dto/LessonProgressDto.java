package ru.offer.hunt.learning.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.LessonProgressStatus;

@Getter
@Setter
public class LessonProgressDto {
  private UUID userId;
  private UUID lessonId;
  private int progressPercentage;
  private LessonProgressStatus status;
  private OffsetDateTime computedAt;
  private Integer contentVersion;
}
