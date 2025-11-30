package ru.offer.hunt.learning.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.offer.hunt.learning.model.entity.AiTaskStats;
import ru.offer.hunt.learning.model.id.AiTaskStatsId;

public interface AiTaskStatsRepository extends JpaRepository<AiTaskStats, AiTaskStatsId> {
}