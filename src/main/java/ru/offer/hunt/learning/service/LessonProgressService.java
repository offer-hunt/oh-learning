package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.offer.hunt.learning.model.dto.LessonProgressDto;
import ru.offer.hunt.learning.model.dto.LessonProgressUpsertRequest;
import ru.offer.hunt.learning.model.entity.LessonProgress;
import ru.offer.hunt.learning.model.enums.LessonProgressStatus;
import ru.offer.hunt.learning.model.id.LessonProgressId;
import ru.offer.hunt.learning.model.mapper.LessonProgressMapper;
import ru.offer.hunt.learning.model.repository.LessonProgressRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonProgressService {

  private final LessonProgressRepository repo;
  private final LessonProgressMapper mapper;

  @Transactional(readOnly = true)
  public LessonProgressDto get(UUID userId, UUID lessonId) {
    var id = new LessonProgressId(userId, lessonId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "LessonProgress not found"));
    return mapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public List<LessonProgressDto> listByUser(UUID userId) {
    return repo.findByIdUserId(userId).stream().map(mapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<LessonProgressDto> listByLesson(UUID lessonId) {
    return repo.findByIdLessonId(lessonId).stream().map(mapper::toDto).toList();
  }

  public LessonProgressDto create(UUID userId, UUID lessonId, LessonProgressUpsertRequest req) {
    LessonProgress entity = mapper.toEntity(userId, lessonId, req);

    if (entity.getProgressPercentage() == 0 && req.getProgressPercentage() == null) {
      entity.setProgressPercentage(0);
    }
    entity.setProgressPercentage(clamp0to100(entity.getProgressPercentage()));

    if (entity.getStatus() == null) {
      entity.setStatus(LessonProgressStatus.NOT_STARTED);
    }

    if (entity.getComputedAt() == null) {
      entity.setComputedAt(OffsetDateTime.now());
    }

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public LessonProgressDto update(UUID userId, UUID lessonId, LessonProgressUpsertRequest req) {
    var id = new LessonProgressId(userId, lessonId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "LessonProgress not found"));

    mapper.update(entity, req);

    entity.setProgressPercentage(clamp0to100(entity.getProgressPercentage()));

    if (entity.getComputedAt() == null) {
      entity.setComputedAt(OffsetDateTime.now());
    }

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public void delete(UUID userId, UUID lessonId) {
    var id = new LessonProgressId(userId, lessonId);
    if (!repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "LessonProgress not found");
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
