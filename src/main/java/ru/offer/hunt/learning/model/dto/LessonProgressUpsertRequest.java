package ru.offer.hunt.learning.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.LessonProgressStatus;

@Getter
@Setter
public class LessonProgressUpsertRequest {
  @Min(0)
  @Max(100)
  private Integer progressPercentage;

  @NotNull private LessonProgressStatus status;

  private OffsetDateTime computedAt;
  private Integer contentVersion;
}
