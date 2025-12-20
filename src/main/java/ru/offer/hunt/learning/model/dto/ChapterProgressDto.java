package ru.offer.hunt.learning.model.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterProgressDto {
  private UUID chapterId;
  private int lessonsTotal;
  private int lessonsCompleted;
  private int avgLessonProgressPercent;
}
