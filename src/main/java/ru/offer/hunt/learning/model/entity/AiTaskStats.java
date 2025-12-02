package ru.offer.hunt.learning.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.offer.hunt.learning.model.id.AiTaskStatsId;

@Entity
@Table(name = "learning_ai_task_stats", schema = "learning")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AiTaskStats {

  @EmbeddedId private AiTaskStatsId id;

  @Column(name = "hints_used")
  private Integer hintsUsed;

  @Column(name = "last_request_at")
  private OffsetDateTime lastRequestAt;
}
