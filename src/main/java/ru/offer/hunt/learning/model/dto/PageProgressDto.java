package ru.offer.hunt.learning.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.PageProgressStatus;

@Getter
@Setter
public class PageProgressDto {
  private UUID userId;
  private UUID pageId;
  private PageProgressStatus status;
  private OffsetDateTime firstViewedAt;
  private OffsetDateTime lastActivityAt;
  private OffsetDateTime completedAt;
  private int timeSpentSec;
  private int attemptsCount;
  private Integer contentVersion;
}
