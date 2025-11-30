package ru.offer.hunt.learning.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AiExternalHintRequest {
    private UUID taskId;
    private String code;
    private Integer previousHintsCount;
}
