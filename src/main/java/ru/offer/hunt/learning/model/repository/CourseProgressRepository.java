package ru.offer.hunt.learning.model.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.offer.hunt.learning.model.entity.CourseProgress;
import ru.offer.hunt.learning.model.id.CourseProgressId;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, CourseProgressId> {
  List<CourseProgress> findByIdUserId(UUID userId);

  List<CourseProgress> findByIdCourseId(UUID courseId);

  List<CourseProgress> findByIdCourseIdIn(Collection<UUID> courseIds);
}
