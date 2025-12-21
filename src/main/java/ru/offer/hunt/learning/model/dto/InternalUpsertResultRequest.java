package ru.offer.hunt.learning.model.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.SubmissionStatus;

@Getter
@Setter
public class InternalUpsertResultRequest {

  @NotNull private UUID userId;

  @NotNull private UUID questionId;

  @NotNull private SubmissionStatus submissionStatus;

  private BigDecimal score;

  private String feedback;
}
