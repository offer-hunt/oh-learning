package ru.offer.hunt.learning.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.offer.hunt.learning.model.enums.SubmissionStatus;
import ru.offer.hunt.learning.model.id.QuestionStateId;

@Entity
@Table(schema = "learning", name = "learning_question_states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QuestionState {

  @EmbeddedId private QuestionStateId id;

  @Column(name = "is_solved", nullable = false)
  private boolean solved;

  @Column(name = "solved_at")
  private OffsetDateTime solvedAt;

  @Column(name = "last_submission_id")
  private UUID lastSubmissionId;

  @Enumerated(EnumType.STRING)
  @Column(name = "last_status")
  private SubmissionStatus lastStatus;

  @Column(name = "last_updated_at", nullable = false)
  private OffsetDateTime lastUpdatedAt;
}
