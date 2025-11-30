package ru.offer.hunt.learning.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseProgressUpsertRequest {
  @Min(0)
  @Max(100)
  private Integer progressPercentage;

  @NotBlank private String status;

  private OffsetDateTime computedAt;
  private OffsetDateTime lastActivityAt;
  private OffsetDateTime completedAt;
  private Integer contentVersion;
}
