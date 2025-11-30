package ru.offer.hunt.learning.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiExternalExplainRequest {
    private String text;
    private String query;
}