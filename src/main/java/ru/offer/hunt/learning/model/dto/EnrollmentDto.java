package ru.offer.hunt.learning.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.EnrollmentSource;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;

@Getter
@Setter
public class EnrollmentDto {
  private UUID userId;
  private UUID courseId;
  private EnrollmentStatus status;
  private EnrollmentSource source;
  private OffsetDateTime enrolledAt;
  private OffsetDateTime startedAt;
  private OffsetDateTime completedAt;
  private OffsetDateTime revokedAt;
  private OffsetDateTime lastActivityAt;
}
