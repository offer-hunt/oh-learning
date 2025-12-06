package ru.offer.hunt.learning.model.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoursesAnalyticsRequest {
  private List<UUID> courseIds;
}
