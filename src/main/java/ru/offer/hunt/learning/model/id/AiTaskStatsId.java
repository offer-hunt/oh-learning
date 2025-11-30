package ru.offer.hunt.learning.model.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class AiTaskStatsId implements Serializable {
    private UUID userId;
    private UUID taskId;
}