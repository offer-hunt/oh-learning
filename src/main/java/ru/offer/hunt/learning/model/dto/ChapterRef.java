package ru.offer.hunt.learning.model.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterRef {
  private UUID chapterId;
  private List<UUID> lessonIds;
}
