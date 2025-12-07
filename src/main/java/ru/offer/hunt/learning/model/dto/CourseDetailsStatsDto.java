package ru.offer.hunt.learning.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDetailsStatsDto {
  private int lessonsTotal;
  private int lessonsNotStarted;
  private int lessonsInProgress;
  private int lessonsCompleted;

  private int pagesTotal;
  private int pagesNotStarted;
  private int pagesInProgress;
  private int pagesCompleted;

  private int questionsTotal;
  private int questionsSolved;

  private int avgLessonProgressPercent;
}
