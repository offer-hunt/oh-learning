package ru.offer.hunt.learning.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiResponseDto {
    private String content;
    private String errorMessage;
    private boolean success;
}