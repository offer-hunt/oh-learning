package ru.offer.hunt.learning.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.SubmissionStatus;

@Getter
@Setter
public class QuestionStateDto {
  private UUID userId;
  private UUID questionId;
  private boolean solved;
  private OffsetDateTime solvedAt;
  private UUID lastSubmissionId;
  private SubmissionStatus lastStatus;
  private OffsetDateTime lastUpdatedAt;
  private BigDecimal lastScore;
  private String lastFeedback;
}
