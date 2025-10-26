package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.offer.hunt.learning.model.dto.QuestionStateDto;
import ru.offer.hunt.learning.model.dto.QuestionStateUpsertRequest;
import ru.offer.hunt.learning.model.entity.QuestionState;
import ru.offer.hunt.learning.model.id.QuestionStateId;
import ru.offer.hunt.learning.model.mapper.QuestionStateMapper;
import ru.offer.hunt.learning.model.repository.QuestionStateRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionStateService {

  private final QuestionStateRepository repo;
  private final QuestionStateMapper mapper;

  @Transactional(readOnly = true)
  public QuestionStateDto get(UUID userId, UUID questionId) {
    var id = new QuestionStateId(userId, questionId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QuestionState not found"));
    return mapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public List<QuestionStateDto> listByUser(UUID userId) {
    return repo.findByIdUserId(userId).stream().map(mapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<QuestionStateDto> listByQuestion(UUID questionId) {
    return repo.findByIdQuestionId(questionId).stream().map(mapper::toDto).toList();
  }

  public QuestionStateDto create(UUID userId, UUID questionId, QuestionStateUpsertRequest req) {
    QuestionState entity = mapper.toEntity(userId, questionId, req);

    if (req.getSolved() == null) {
      entity.setSolved(false);
    }
    if (entity.isSolved() && entity.getSolvedAt() == null) {
      entity.setSolvedAt(OffsetDateTime.now());
    }
    entity.setLastUpdatedAt(OffsetDateTime.now());

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public QuestionStateDto update(UUID userId, UUID questionId, QuestionStateUpsertRequest req) {
    var id = new QuestionStateId(userId, questionId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QuestionState not found"));

    mapper.update(entity, req);

    if (entity.isSolved() && entity.getSolvedAt() == null) {
      entity.setSolvedAt(OffsetDateTime.now());
    }
    entity.setLastUpdatedAt(OffsetDateTime.now());

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public void delete(UUID userId, UUID questionId) {
    var id = new QuestionStateId(userId, questionId);
    if (!repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "QuestionState not found");
    }
    repo.deleteById(id);
  }
}
