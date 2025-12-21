package ru.offer.hunt.learning.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageCompleteRequest {

  @NotNull private UUID courseId;
}
