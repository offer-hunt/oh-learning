package ru.offer.hunt.learning.model.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.offer.hunt.learning.model.entity.QuestionState;
import ru.offer.hunt.learning.model.id.QuestionStateId;

public interface QuestionStateRepository extends JpaRepository<QuestionState, QuestionStateId> {
  List<QuestionState> findByIdUserId(UUID userId);

  List<QuestionState> findByIdQuestionId(UUID questionId);

  List<QuestionState> findByIdUserIdAndIdQuestionIdIn(UUID userId, Collection<UUID> questionIds);
}
