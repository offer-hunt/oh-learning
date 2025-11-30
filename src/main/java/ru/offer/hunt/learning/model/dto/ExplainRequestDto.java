package ru.offer.hunt.learning.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на объяснение материала")
public class ExplainRequestDto {

    @Schema(description = "ID методического материала")
    private UUID materialId;

    @Schema(description = "Текст, который выделил студент")
    private String selectedText;

    @Schema(description = "Вопрос студента к этому тексту", example = "Объясни простыми словами, как это работает в Java")
    private String userQuery;
}