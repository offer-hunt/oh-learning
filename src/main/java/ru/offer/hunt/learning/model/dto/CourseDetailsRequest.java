package ru.offer.hunt.learning.model.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDetailsRequest {
  private List<ChapterRef> chapters;
  private List<UUID> lessonIds;
  private List<UUID> pageIds;
  private List<UUID> questionIds;
}
