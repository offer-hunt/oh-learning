package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.offer.hunt.learning.model.dto.EnrollmentDto;
import ru.offer.hunt.learning.model.dto.EnrollmentUpsertRequest;
import ru.offer.hunt.learning.model.entity.LearningEnrollment;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;
import ru.offer.hunt.learning.model.id.EnrollmentId;
import ru.offer.hunt.learning.model.mapper.EnrollmentMapper;
import ru.offer.hunt.learning.model.repository.LearningEnrollmentRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EnrollmentService {

  private final LearningEnrollmentRepository repo;
  private final EnrollmentMapper mapper;

  @Transactional(readOnly = true)
  public EnrollmentDto get(UUID userId, UUID courseId) {
    var id = new EnrollmentId(userId, courseId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
    return mapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public List<EnrollmentDto> listByUser(UUID userId) {
    return repo.findByIdUserId(userId).stream().map(mapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<EnrollmentDto> listByCourse(UUID courseId) {
    return repo.findByIdCourseId(courseId).stream().map(mapper::toDto).toList();
  }

  public EnrollmentDto create(UUID userId, UUID courseId, EnrollmentUpsertRequest req) {
    var id = new EnrollmentId(userId, courseId);
    if (repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Enrollment already exists");
    }

    LearningEnrollment entity = mapper.toEntity(userId, courseId, req);
    if (entity.getEnrolledAt() == null) {
      entity.setEnrolledAt(OffsetDateTime.now());
    }
    repo.save(entity);
    return mapper.toDto(entity);
  }

  public EnrollmentDto update(UUID userId, UUID courseId, EnrollmentUpsertRequest req) {
    var id = new EnrollmentId(userId, courseId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

    mapper.update(entity, req);
    if (entity.getEnrolledAt() == null) {
      entity.setEnrolledAt(OffsetDateTime.now());
    }
    repo.save(entity);
    return mapper.toDto(entity);
  }

  public void delete(UUID userId, UUID courseId) {
    var id = new EnrollmentId(userId, courseId);
    if (!repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found");
    }
    repo.deleteById(id);
  }

  public void revoke(UUID userId, UUID courseId) {
    var id = new EnrollmentId(userId, courseId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

    // делаем идемпотентно
    if (entity.getStatus() == EnrollmentStatus.REVOKED) {
      log.info("Enrollment already revoked, userId={}, courseId={}", userId, courseId);
      return;
    }

    var now = OffsetDateTime.now();
    entity.setStatus(EnrollmentStatus.REVOKED);
    entity.setRevokedAt(now);
    entity.setLastActivityAt(now);

    repo.save(entity);

    log.info("Enrollment revoked, userId={}, courseId={}", userId, courseId);
  }
}
