package ru.offer.hunt.learning.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.PageProgressStatus;

@Getter
@Setter
public class PageProgressUpsertRequest {
  @NotNull private PageProgressStatus status;

  private OffsetDateTime firstViewedAt;
  private OffsetDateTime lastActivityAt;
  private OffsetDateTime completedAt;

  @Min(0)
  private Integer timeSpentSec;

  @Min(0)
  private Integer attemptsCount;

  private Integer contentVersion;
}
