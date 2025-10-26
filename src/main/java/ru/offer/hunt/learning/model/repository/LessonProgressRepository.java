package ru.offer.hunt.learning.model.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.offer.hunt.learning.model.entity.LessonProgress;
import ru.offer.hunt.learning.model.id.LessonProgressId;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, LessonProgressId> {
  List<LessonProgress> findByIdUserId(UUID userId);

  List<LessonProgress> findByIdLessonId(UUID lessonId);
}
