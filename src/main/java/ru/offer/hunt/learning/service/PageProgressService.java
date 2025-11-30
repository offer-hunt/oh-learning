package ru.offer.hunt.learning.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.offer.hunt.learning.model.dto.PageProgressDto;
import ru.offer.hunt.learning.model.dto.PageProgressUpsertRequest;
import ru.offer.hunt.learning.model.entity.PageProgress;
import ru.offer.hunt.learning.model.id.PageProgressId;
import ru.offer.hunt.learning.model.mapper.PageProgressMapper;
import ru.offer.hunt.learning.model.repository.PageProgressRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PageProgressService {

  private final PageProgressRepository repo;
  private final PageProgressMapper mapper;

  @Transactional(readOnly = true)
  public PageProgressDto get(UUID userId, UUID pageId) {
    var id = new PageProgressId(userId, pageId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PageProgress not found"));
    return mapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public List<PageProgressDto> listByUser(UUID userId) {
    return repo.findByIdUserId(userId).stream().map(mapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<PageProgressDto> listByPage(UUID pageId) {
    return repo.findByIdPageId(pageId).stream().map(mapper::toDto).toList();
  }

  public PageProgressDto create(UUID userId, UUID pageId, PageProgressUpsertRequest req) {
    PageProgress entity = mapper.toEntity(userId, pageId, req);

    if (entity.getTimeSpentSec() < 0) {
      entity.setTimeSpentSec(0);
    }
    if (entity.getAttemptsCount() < 0) {
      entity.setAttemptsCount(0);
    }

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public PageProgressDto update(UUID userId, UUID pageId, PageProgressUpsertRequest req) {
    var id = new PageProgressId(userId, pageId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PageProgress not found"));

    mapper.update(entity, req);

    if (entity.getTimeSpentSec() < 0) {
      entity.setTimeSpentSec(0);
    }
    if (entity.getAttemptsCount() < 0) {
      entity.setAttemptsCount(0);
    }

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public void delete(UUID userId, UUID pageId) {
    var id = new PageProgressId(userId, pageId);
    if (!repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PageProgress not found");
    }
    repo.deleteById(id);
  }
}
