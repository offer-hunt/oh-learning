package ru.offer.hunt.learning.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseAnalyticsDto {
  private UUID courseId;
  private long studentsTotal;
  private long studentsActiveLastWeek;
  private long studentsCompleted;
  private int completionPercent;
  private int avgProgressPercent;
  private OffsetDateTime calculatedAt;
}
