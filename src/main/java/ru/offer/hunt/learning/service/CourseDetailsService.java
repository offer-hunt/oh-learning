package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.offer.hunt.learning.model.dto.ChapterProgressDto;
import ru.offer.hunt.learning.model.dto.ChapterRef;
import ru.offer.hunt.learning.model.dto.CourseDetailsRequest;
import ru.offer.hunt.learning.model.dto.CourseDetailsStatsDto;
import ru.offer.hunt.learning.model.dto.CourseProgressDetailsDto;
import ru.offer.hunt.learning.model.entity.CourseProgress;
import ru.offer.hunt.learning.model.entity.LessonProgress;
import ru.offer.hunt.learning.model.entity.PageProgress;
import ru.offer.hunt.learning.model.entity.QuestionState;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;
import ru.offer.hunt.learning.model.enums.LessonProgressStatus;
import ru.offer.hunt.learning.model.id.CourseProgressId;
import ru.offer.hunt.learning.model.id.EnrollmentId;
import ru.offer.hunt.learning.model.repository.CourseProgressRepository;
import ru.offer.hunt.learning.model.repository.LearningEnrollmentRepository;
import ru.offer.hunt.learning.model.repository.LessonProgressRepository;
import ru.offer.hunt.learning.model.repository.PageProgressRepository;
import ru.offer.hunt.learning.model.repository.QuestionStateRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseDetailsService {

  private final LearningEnrollmentRepository enrollmentRepo;
  private final CourseProgressRepository courseProgressRepo;
  private final LessonProgressRepository lessonProgressRepo;
  private final PageProgressRepository pageProgressRepo;
  private final QuestionStateRepository questionStateRepo;

  public CourseProgressDetailsDto details(UUID userId, UUID courseId, CourseDetailsRequest req) {
    var enrollment =
        enrollmentRepo
            .findById(new EnrollmentId(userId, courseId))
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

    var courseProgressOpt = courseProgressRepo.findById(new CourseProgressId(userId, courseId));

    List<UUID> lessonIds = safeList(req != null ? req.getLessonIds() : null);
    List<UUID> pageIds = safeList(req != null ? req.getPageIds() : null);
    List<UUID> questionIds = safeList(req != null ? req.getQuestionIds() : null);
    List<ChapterRef> chapters = safeList(req != null ? req.getChapters() : null);

    Map<UUID, LessonProgress> lessonById = Collections.emptyMap();
    if (!lessonIds.isEmpty()) {
      lessonById =
          lessonProgressRepo.findByIdUserIdAndIdLessonIdIn(userId, lessonIds).stream()
              .collect(Collectors.toMap(p -> p.getId().getLessonId(), p -> p, (a, b) -> a));
    }

    Map<UUID, PageProgress> pageById = Collections.emptyMap();
    if (!pageIds.isEmpty()) {
      pageById =
          pageProgressRepo.findByIdUserIdAndIdPageIdIn(userId, pageIds).stream()
              .collect(Collectors.toMap(p -> p.getId().getPageId(), p -> p, (a, b) -> a));
    }

    Map<UUID, QuestionState> qsById = Collections.emptyMap();
    if (!questionIds.isEmpty()) {
      qsById =
          questionStateRepo.findByIdUserIdAndIdQuestionIdIn(userId, questionIds).stream()
              .collect(Collectors.toMap(q -> q.getId().getQuestionId(), q -> q, (a, b) -> a));
    }

    // --- stats ---
    CourseDetailsStatsDto stats = new CourseDetailsStatsDto();

    // lessons
    stats.setLessonsTotal(lessonIds.size());
    int lessonsCompleted = 0;
    int lessonsInProgress = 0;
    int lessonsNotStarted = 0;
    long sumProgress = 0;

    for (UUID id : lessonIds) {
      LessonProgress lp = lessonById.get(id);
      int perc = lp != null ? lp.getProgressPercentage() : 0;
      LessonProgressStatus st =
          lp != null && lp.getStatus() != null ? lp.getStatus() : LessonProgressStatus.NOT_STARTED;

      sumProgress += perc;

      if (st == LessonProgressStatus.COMPLETED) {
        lessonsCompleted++;
      } else if (st == LessonProgressStatus.IN_PROGRESS || perc > 0) {
        lessonsInProgress++;
      } else {
        lessonsNotStarted++;
      }
    }

    stats.setLessonsCompleted(lessonsCompleted);
    stats.setLessonsInProgress(lessonsInProgress);
    stats.setLessonsNotStarted(lessonsNotStarted);
    stats.setAvgLessonProgressPercent(
        lessonIds.isEmpty() ? 0 : (int) Math.round(sumProgress * 1.0 / lessonIds.size()));

    // pages
    stats.setPagesTotal(pageIds.size());
    int pagesCompleted = 0;
    int pagesInProgress = 0;
    int pagesNotStarted = 0;
    for (UUID id : pageIds) {
      PageProgress pp = pageById.get(id);
      var st =
          pp != null && pp.getStatus() != null
              ? pp.getStatus()
              : ru.offer.hunt.learning.model.enums.PageProgressStatus.NOT_STARTED;
      if (st == ru.offer.hunt.learning.model.enums.PageProgressStatus.COMPLETED) {
        pagesCompleted++;
      } else if (st == ru.offer.hunt.learning.model.enums.PageProgressStatus.IN_PROGRESS) {
        pagesInProgress++;
      } else {
        pagesNotStarted++;
      }
    }
    stats.setPagesCompleted(pagesCompleted);
    stats.setPagesInProgress(pagesInProgress);
    stats.setPagesNotStarted(pagesNotStarted);

    // questions
    stats.setQuestionsTotal(questionIds.size());
    int solved = 0;
    for (UUID id : questionIds) {
      QuestionState qs = qsById.get(id);
      if (qs != null && qs.isSolved()) {
        solved++;
      }
    }
    stats.setQuestionsSolved(solved);

    // --- computed status ---
    LessonProgressStatus computed;
    if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
      computed = LessonProgressStatus.COMPLETED;
    } else if (stats.getAvgLessonProgressPercent() > 0
        || stats.getLessonsInProgress() > 0
        || stats.getPagesInProgress() > 0) {
      computed = LessonProgressStatus.IN_PROGRESS;
    } else {
      computed = LessonProgressStatus.NOT_STARTED;
    }

    // --- last activity ---
    OffsetDateTime last = enrollment.getLastActivityAt();

    if (courseProgressOpt.isPresent() && courseProgressOpt.get().getLastActivityAt() != null) {
      last = max(last, courseProgressOpt.get().getLastActivityAt());
    }

    for (LessonProgress lp : lessonById.values()) {
      last = max(last, lp.getComputedAt());
    }
    for (PageProgress pp : pageById.values()) {
      last = max(last, pp.getLastActivityAt());
      last = max(last, pp.getCompletedAt());
    }
    for (QuestionState qs : qsById.values()) {
      last = max(last, qs.getLastUpdatedAt());
      last = max(last, qs.getSolvedAt());
    }

    // --- chapter progress ---
    List<ChapterProgressDto> chapterDtos = new ArrayList<>();
    for (ChapterRef ch : chapters) {
      List<UUID> chLessons = safeList(ch.getLessonIds());
      if (chLessons.isEmpty()) {
        continue;
      }

      long chSum = 0;
      int chCompleted = 0;
      for (UUID lid : chLessons) {
        LessonProgress lp = lessonById.get(lid);
        int p = lp != null ? lp.getProgressPercentage() : 0;
        var st =
            lp != null && lp.getStatus() != null
                ? lp.getStatus()
                : LessonProgressStatus.NOT_STARTED;
        chSum += p;
        if (st == LessonProgressStatus.COMPLETED) {
          chCompleted++;
        }
      }

      ChapterProgressDto dto = new ChapterProgressDto();
      dto.setChapterId(ch.getChapterId());
      dto.setLessonsTotal(chLessons.size());
      dto.setLessonsCompleted(chCompleted);
      dto.setAvgLessonProgressPercent((int) Math.round(chSum * 1.0 / chLessons.size()));
      chapterDtos.add(dto);
    }

    // --- response ---
    CourseProgressDetailsDto out = new CourseProgressDetailsDto();
    out.setCourseId(courseId);
    out.setEnrollmentStatus(enrollment.getStatus());
    out.setEnrolledAt(enrollment.getEnrolledAt());
    out.setCompletedAt(enrollment.getCompletedAt());
    out.setLastActivityAt(last);
    out.setComputedStatus(computed);
    out.setStats(stats);
    out.setChapters(chapterDtos);

    Integer cp = courseProgressOpt.map(CourseProgress::getProgressPercentage).orElse(null);
    out.setCourseProgressPercent(cp != null ? cp : stats.getAvgLessonProgressPercent());

    return out;
  }

  private static <T> List<T> safeList(List<T> v) {
    return v == null ? List.of() : v;
  }

  private static OffsetDateTime max(OffsetDateTime a, OffsetDateTime b) {
    if (a == null) {
      return b;
    }
    if (b == null) {
      return a;
    }
    return a.isAfter(b) ? a : b;
  }
}
