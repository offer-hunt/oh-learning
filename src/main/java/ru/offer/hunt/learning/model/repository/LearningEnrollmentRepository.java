package ru.offer.hunt.learning.model.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.offer.hunt.learning.model.entity.LearningEnrollment;
import ru.offer.hunt.learning.model.id.EnrollmentId;

public interface LearningEnrollmentRepository
    extends JpaRepository<LearningEnrollment, EnrollmentId> {
  List<LearningEnrollment> findByIdUserId(UUID userId);

  List<LearningEnrollment> findByIdCourseId(UUID courseId);
}
