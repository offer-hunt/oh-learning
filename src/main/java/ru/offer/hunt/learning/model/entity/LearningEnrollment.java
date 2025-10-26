package ru.offer.hunt.learning.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.EnrollmentSource;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;
import ru.offer.hunt.learning.model.id.EnrollmentId;

@Entity
@Table(schema = "learning", name = "learning_enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LearningEnrollment {

  @EmbeddedId @EqualsAndHashCode.Include private EnrollmentId id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private EnrollmentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "source", nullable = false)
  private EnrollmentSource source;

  @Column(name = "enrolled_at", nullable = false)
  private OffsetDateTime enrolledAt;

  @Column(name = "started_at")
  private OffsetDateTime startedAt;

  @Column(name = "completed_at")
  private OffsetDateTime completedAt;

  @Column(name = "revoked_at")
  private OffsetDateTime revokedAt;

  @Column(name = "last_activity_at")
  private OffsetDateTime lastActivityAt;
}
