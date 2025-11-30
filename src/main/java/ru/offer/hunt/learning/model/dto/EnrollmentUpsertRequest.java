package ru.offer.hunt.learning.model.dto;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.EnrollmentSource;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;

@Getter
@Setter
public class EnrollmentUpsertRequest {
  @NotNull private EnrollmentStatus status;

  @NotNull private EnrollmentSource source;

  private OffsetDateTime enrolledAt;
  private OffsetDateTime startedAt;
  private OffsetDateTime completedAt;
  private OffsetDateTime revokedAt;
  private OffsetDateTime lastActivityAt;
}
