package ru.offer.hunt.learning.model.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiExternalHintRequest {
  private UUID taskId;
  private String code;
  private Integer previousHintsCount;
}
