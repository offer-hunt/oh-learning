package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.offer.hunt.learning.model.dto.LearningRatingDto;
import ru.offer.hunt.learning.model.dto.LearningRatingUpsertRequest;
import ru.offer.hunt.learning.model.entity.LearningRating;
import ru.offer.hunt.learning.model.id.RatingId;
import ru.offer.hunt.learning.model.mapper.LearningRatingMapper;
import ru.offer.hunt.learning.model.repository.LearningRatingRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LearningRatingService {

  private final LearningRatingRepository repo;
  private final LearningRatingMapper mapper;

  @Transactional(readOnly = true)
  public LearningRatingDto get(UUID userId, UUID courseId) {
    var id = new RatingId(userId, courseId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "LearningRating not found"));
    return mapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public List<LearningRatingDto> listByUser(UUID userId) {
    return repo.findByIdUserId(userId).stream().map(mapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<LearningRatingDto> listByCourse(UUID courseId) {
    return repo.findByIdCourseId(courseId).stream().map(mapper::toDto).toList();
  }

  public LearningRatingDto create(UUID userId, UUID courseId, LearningRatingUpsertRequest req) {
    if (req.getValue() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "value is required");
    }
    validateValue(req.getValue());

    var id = new RatingId(userId, courseId);
    if (repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "LearningRating already exists");
    }

    LearningRating entity = mapper.toEntity(userId, courseId, req);
    entity.setValue(req.getValue());
    entity.setCreatedAt(OffsetDateTime.now());
    entity.setUpdatedAt(entity.getCreatedAt());

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public LearningRatingDto update(UUID userId, UUID courseId, LearningRatingUpsertRequest req) {
    var id = new RatingId(userId, courseId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "LearningRating not found"));

    mapper.update(entity, req);

    if (req.getValue() != null) {
      validateValue(entity.getValue());
    }
    entity.setUpdatedAt(OffsetDateTime.now());

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public void delete(UUID userId, UUID courseId) {
    var id = new RatingId(userId, courseId);
    if (!repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "LearningRating not found");
    }
    repo.deleteById(id);
  }

  private static void validateValue(Integer v) {
    if (v == null || v < 1 || v > 5) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "value must be in range 1..5");
    }
  }
}
