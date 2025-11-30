package ru.offer.hunt.learning.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.PageProgressStatus;
import ru.offer.hunt.learning.model.id.PageProgressId;

@Entity
@Table(schema = "learning", name = "learning_page_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PageProgress {

  @EmbeddedId @EqualsAndHashCode.Include private PageProgressId id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PageProgressStatus status;

  @Column(name = "first_viewed_at")
  private OffsetDateTime firstViewedAt;

  @Column(name = "last_activity_at")
  private OffsetDateTime lastActivityAt;

  @Column(name = "completed_at")
  private OffsetDateTime completedAt;

  @Min(0)
  @Column(name = "time_spent_sec", nullable = false)
  @Builder.Default
  private int timeSpentSec = 0;

  @Min(0)
  @Column(name = "attempts_count", nullable = false)
  @Builder.Default
  private int attemptsCount = 0;

  @Column(name = "content_version")
  private Integer contentVersion;
}
