package ru.offer.hunt.learning.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.offer.hunt.learning.model.id.AiTaskStatsId; // Создадим ниже

@Entity
@Table(name = "learning_ai_task_stats", schema = "learning")
@Data
@NoArgsConstructor
@IdClass(AiTaskStatsId.class) // Композитный ключ
public class AiTaskStats {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @Id
  @Column(name = "task_id")
  private UUID taskId;

  @Column(name = "hints_used")
  private Integer hintsUsed;

  @Column(name = "last_request_at")
  private OffsetDateTime lastRequestAt;
}
