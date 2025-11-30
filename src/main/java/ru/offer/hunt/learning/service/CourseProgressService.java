package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.offer.hunt.learning.model.dto.CourseProgressDto;
import ru.offer.hunt.learning.model.dto.CourseProgressUpsertRequest;
import ru.offer.hunt.learning.model.entity.CourseProgress;
import ru.offer.hunt.learning.model.id.CourseProgressId;
import ru.offer.hunt.learning.model.mapper.CourseProgressMapper;
import ru.offer.hunt.learning.model.repository.CourseProgressRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseProgressService {

  private final CourseProgressRepository repo;
  private final CourseProgressMapper mapper;

  @Transactional(readOnly = true)
  public CourseProgressDto get(UUID userId, UUID courseId) {
    var id = new CourseProgressId(userId, courseId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "CourseProgress not found"));
    return mapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public List<CourseProgressDto> listByUser(UUID userId) {
    return repo.findByIdUserId(userId).stream().map(mapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<CourseProgressDto> listByCourse(UUID courseId) {
    return repo.findByIdCourseId(courseId).stream().map(mapper::toDto).toList();
  }

  public CourseProgressDto create(UUID userId, UUID courseId, CourseProgressUpsertRequest req) {
    if (req.getStatus() == null || req.getStatus().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
    }

    CourseProgress entity = mapper.toEntity(userId, courseId, req);

    if (entity.getComputedAt() == null) {
      entity.setComputedAt(OffsetDateTime.now());
    }
    if (entity.getProgressPercentage() == 0 && req.getProgressPercentage() == null) {
      entity.setProgressPercentage(0);
    }
    entity.setProgressPercentage(clamp0to100(entity.getProgressPercentage()));

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public CourseProgressDto update(UUID userId, UUID courseId, CourseProgressUpsertRequest req) {
    var id = new CourseProgressId(userId, courseId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "CourseProgress not found"));

    mapper.update(entity, req);

    if (entity.getComputedAt() == null) {
      entity.setComputedAt(OffsetDateTime.now());
    }
    entity.setProgressPercentage(clamp0to100(entity.getProgressPercentage()));

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public void delete(UUID userId, UUID courseId) {
    var id = new CourseProgressId(userId, courseId);
    if (!repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CourseProgress not found");
    }
    repo.deleteById(id);
  }

  private static int clamp0to100(Integer value) {
    if (value == null) {
      return 0;
    }
    if (value < 0) {
      return 0;
    }
    if (value > 100) {
      return 100;
    }
    return value;
  }
}
