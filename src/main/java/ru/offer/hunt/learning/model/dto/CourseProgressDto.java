package ru.offer.hunt.learning.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseProgressDto {
  private UUID userId;
  private UUID courseId;
  private int progressPercentage;
  private String status;
  private OffsetDateTime computedAt;
  private OffsetDateTime lastActivityAt;
  private OffsetDateTime completedAt;
  private Integer contentVersion;
}
