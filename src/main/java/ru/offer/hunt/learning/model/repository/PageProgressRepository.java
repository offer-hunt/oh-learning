package ru.offer.hunt.learning.model.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.offer.hunt.learning.model.entity.PageProgress;
import ru.offer.hunt.learning.model.id.PageProgressId;

public interface PageProgressRepository extends JpaRepository<PageProgress, PageProgressId> {
  List<PageProgress> findByIdUserId(UUID userId);

  List<PageProgress> findByIdPageId(UUID pageId);

  List<PageProgress> findByIdUserIdAndIdPageIdIn(UUID userId, Collection<UUID> pageIds);
}
