package ru.offer.hunt.learning.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.offer.hunt.learning.model.id.CourseProgressId;

@Entity
@Table(schema = "learning", name = "learning_course_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CourseProgress {

  @EmbeddedId private CourseProgressId id;

  @Min(0)
  @Max(100)
  @Column(name = "progress_percentage", nullable = false)
  private int progressPercentage;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "computed_at", nullable = false)
  private OffsetDateTime computedAt;

  @Column(name = "last_activity_at")
  private OffsetDateTime lastActivityAt;

  @Column(name = "completed_at")
  private OffsetDateTime completedAt;

  @Column(name = "content_version")
  private Integer contentVersion;
}
