package ru.offer.hunt.learning.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ru.offer.hunt.learning.model.enums.LessonProgressStatus;
import ru.offer.hunt.learning.model.id.LessonProgressId;

@Entity
@Table(schema = "learning", name = "learning_lesson_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LessonProgress {

  @EmbeddedId private LessonProgressId id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private LessonProgressStatus status;

  @Min(0)
  @Max(100)
  @Column(name = "progress_percentage", nullable = false)
  private int progressPercentage;

  @Column(name = "computed_at", nullable = false)
  private OffsetDateTime computedAt;

  @Column(name = "content_version")
  private Integer contentVersion;
}
