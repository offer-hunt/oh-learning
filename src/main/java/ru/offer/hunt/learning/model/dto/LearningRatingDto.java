package ru.offer.hunt.learning.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningRatingDto {
  private UUID userId;
  private UUID courseId;
  private int value;
  private String comment;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
