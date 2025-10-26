package ru.offer.hunt.learning.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearningRatingUpsertRequest {
  @NotNull
  @Min(1)
  @Max(5)
  private Integer value;

  private String comment;
}
