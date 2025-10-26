package ru.offer.hunt.learning.model.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.offer.hunt.learning.model.entity.LearningRating;
import ru.offer.hunt.learning.model.id.RatingId;

public interface LearningRatingRepository extends JpaRepository<LearningRating, RatingId> {
  List<LearningRating> findByIdUserId(UUID userId);

  List<LearningRating> findByIdCourseId(UUID courseId);
}
