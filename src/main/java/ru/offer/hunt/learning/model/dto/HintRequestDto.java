package ru.offer.hunt.learning.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на подсказку по задаче")
public class HintRequestDto {
  @Schema(
      description = "Текущий код решения студента",
      example = "public class Main { public static void main(String[] args) { ... } }")
  private String currentCode;
}
