package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.offer.hunt.learning.model.dto.CourseStructureDto;
import ru.offer.hunt.learning.model.dto.PageCompleteRequest;
import ru.offer.hunt.learning.model.dto.PageProgressDto;
import ru.offer.hunt.learning.model.dto.PageProgressUpsertRequest;
import ru.offer.hunt.learning.model.entity.CourseProgress;
import ru.offer.hunt.learning.model.entity.LearningEnrollment;
import ru.offer.hunt.learning.model.entity.LessonProgress;
import ru.offer.hunt.learning.model.entity.PageProgress;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;
import ru.offer.hunt.learning.model.enums.LessonProgressStatus;
import ru.offer.hunt.learning.model.enums.PageProgressStatus;
import ru.offer.hunt.learning.model.id.CourseProgressId;
import ru.offer.hunt.learning.model.id.EnrollmentId;
import ru.offer.hunt.learning.model.id.LessonProgressId;
import ru.offer.hunt.learning.model.id.PageProgressId;
import ru.offer.hunt.learning.model.mapper.PageProgressMapper;
import ru.offer.hunt.learning.model.repository.CourseProgressRepository;
import ru.offer.hunt.learning.model.repository.LearningEnrollmentRepository;
import ru.offer.hunt.learning.model.repository.LessonProgressRepository;
import ru.offer.hunt.learning.model.repository.PageProgressRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PageProgressService {

  private final PageProgressRepository repo;
  private final LessonProgressRepository lessonProgressRepo;
  private final CourseProgressRepository courseProgressRepo;
  private final LearningEnrollmentRepository enrollmentRepo;
  private final CourseServiceClient courseServiceClient;
  private final PageProgressMapper mapper;

  @Transactional(readOnly = true)
  public PageProgressDto get(UUID userId, UUID pageId) {
    var id = new PageProgressId(userId, pageId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PageProgress not found"));
    return mapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public List<PageProgressDto> listByUser(UUID userId) {
    return repo.findByIdUserId(userId).stream().map(mapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<PageProgressDto> listByPage(UUID pageId) {
    return repo.findByIdPageId(pageId).stream().map(mapper::toDto).toList();
  }

  public PageProgressDto create(UUID userId, UUID pageId, PageProgressUpsertRequest req) {
    PageProgress entity = mapper.toEntity(userId, pageId, req);

    if (entity.getTimeSpentSec() < 0) {
      entity.setTimeSpentSec(0);
    }
    if (entity.getAttemptsCount() < 0) {
      entity.setAttemptsCount(0);
    }

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public PageProgressDto update(UUID userId, UUID pageId, PageProgressUpsertRequest req) {
    var id = new PageProgressId(userId, pageId);
    var entity =
        repo.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PageProgress not found"));

    mapper.update(entity, req);

    if (entity.getTimeSpentSec() < 0) {
      entity.setTimeSpentSec(0);
    }
    if (entity.getAttemptsCount() < 0) {
      entity.setAttemptsCount(0);
    }

    repo.save(entity);
    return mapper.toDto(entity);
  }

  public void delete(UUID userId, UUID pageId) {
    var id = new PageProgressId(userId, pageId);
    if (!repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PageProgress not found");
    }
    repo.deleteById(id);
  }

  /** POST /api/v1/pages/{pageId}/complete */
  public PageProgressDto completePage(UUID userId, UUID pageId, PageCompleteRequest req) {
    OffsetDateTime now = OffsetDateTime.now();
    UUID courseId = req.getCourseId();

    // 1) проверяем enrollment
    LearningEnrollment enrollment =
        enrollmentRepo
            .findById(new EnrollmentId(userId, courseId))
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

    if (enrollment.getStatus() == EnrollmentStatus.REVOKED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Enrollment revoked");
    }

    // 2) тянем структуру курса из Course Service
    CourseStructureDto structure;
    try {
      structure = courseServiceClient.getCourseStructure(courseId);
    } catch (Exception e) {
      log.error(
          "Course Service structure fetch failed: courseId={}, userId={}", courseId, userId, e);
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE, "Course Service unavailable");
    }

    // 3) определяем lessonId для pageId + список pageIds в уроке + список lessonIds в курсе
    ResolvedCourseContext ctx = resolveContextOrThrow(structure, pageId);
    UUID lessonId = ctx.lessonId();
    List<UUID> lessonPageIds = ctx.lessonPageIds();
    List<UUID> courseLessonIds = ctx.courseLessonIds();

    // 4) upsert PageProgress -> COMPLETED
    PageProgressId ppId = new PageProgressId(userId, pageId);
    PageProgress pp =
        repo.findById(ppId)
            .orElseGet(
                () ->
                    PageProgress.builder()
                        .id(ppId)
                        .status(PageProgressStatus.NOT_STARTED)
                        .timeSpentSec(0)
                        .attemptsCount(0)
                        .build());

    if (pp.getFirstViewedAt() == null) {
      pp.setFirstViewedAt(now);
    }
    pp.setLastActivityAt(now);
    pp.setStatus(PageProgressStatus.COMPLETED);
    if (pp.getCompletedAt() == null) {
      pp.setCompletedAt(now);
    }

    if (pp.getTimeSpentSec() < 0) {
      pp.setTimeSpentSec(0);
    }
    if (pp.getAttemptsCount() < 0) {
      pp.setAttemptsCount(0);
    }

    repo.save(pp);

    // 5) пересчитать LessonProgress (по страницам урока)
    LessonProgress recalculatedLesson = recalcLesson(userId, lessonId, lessonPageIds, now);

    // 6) пересчитать CourseProgress (по урокам курса)
    CourseProgress recalculatedCourse = recalcCourse(userId, courseId, courseLessonIds, now);

    // 7) обновить enrollment
    enrollment.setLastActivityAt(now);
    if (enrollment.getStartedAt() == null) {
      enrollment.setStartedAt(now);
    }

    if ("COMPLETED".equals(recalculatedCourse.getStatus())
        && enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
      enrollment.setStatus(EnrollmentStatus.COMPLETED);
      if (enrollment.getCompletedAt() == null) {
        enrollment.setCompletedAt(now);
      }
    }

    enrollmentRepo.save(enrollment);

    log.info(
        "Page completed: userId={}, courseId={}, lessonId={}, pageId={}, lesson%={}, course%={}",
        userId,
        courseId,
        lessonId,
        pageId,
        recalculatedLesson.getProgressPercentage(),
        recalculatedCourse.getProgressPercentage());

    return mapper.toDto(pp);
  }

  /** Находим в структуре: lessonId для pageId, lessonPageIds, courseLessonIds */
  private ResolvedCourseContext resolveContextOrThrow(CourseStructureDto structure, UUID pageId) {
    if (structure == null || structure.lessons() == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course structure not found");
    }

    List<UUID> courseLessonIds =
        structure.lessons().stream().map(CourseStructureDto.LessonDto::lessonId).toList();

    for (var lesson : structure.lessons()) {
      if (lesson.pages() == null) {
        continue;
      }

      boolean contains = lesson.pages().stream().anyMatch(p -> pageId.equals(p.pageId()));
      if (contains) {
        List<UUID> lessonPageIds =
            lesson.pages().stream().map(CourseStructureDto.PageDto::pageId).toList();
        return new ResolvedCourseContext(lesson.lessonId(), lessonPageIds, courseLessonIds);
      }
    }

    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found in course");
  }

  private record ResolvedCourseContext(
      UUID lessonId, List<UUID> lessonPageIds, List<UUID> courseLessonIds) {}

  /** Пересчёт LessonProgress: % = completedPages / totalPages */
  private LessonProgress recalcLesson(
      UUID userId, UUID lessonId, List<UUID> lessonPageIds, OffsetDateTime now) {

    int total = lessonPageIds.size();
    if (total == 0) {
      return upsertLesson(userId, lessonId, 0, LessonProgressStatus.NOT_STARTED, now);
    }

    List<PageProgress> stored = repo.findByIdUserIdAndIdPageIdIn(userId, lessonPageIds);

    Map<UUID, PageProgressStatus> statusByPage =
        stored.stream()
            .collect(
                Collectors.toMap(
                    p -> p.getId().getPageId(),
                    p -> p.getStatus() != null ? p.getStatus() : PageProgressStatus.NOT_STARTED,
                    (a, b) -> a));

    long completed =
        lessonPageIds.stream()
            .map(pid -> statusByPage.getOrDefault(pid, PageProgressStatus.NOT_STARTED))
            .filter(st -> st == PageProgressStatus.COMPLETED)
            .count();

    long inProgress =
        lessonPageIds.stream()
            .map(pid -> statusByPage.getOrDefault(pid, PageProgressStatus.NOT_STARTED))
            .filter(st -> st == PageProgressStatus.IN_PROGRESS)
            .count();

    int perc = clamp0to100((int) Math.round(completed * 100.0 / total));

    LessonProgressStatus st;
    if (completed == total) {
      st = LessonProgressStatus.COMPLETED;
    } else if (completed > 0 || inProgress > 0) {
      st = LessonProgressStatus.IN_PROGRESS;
    } else {
      st = LessonProgressStatus.NOT_STARTED;
    }

    return upsertLesson(userId, lessonId, perc, st, now);
  }

  private LessonProgress upsertLesson(
      UUID userId, UUID lessonId, int perc, LessonProgressStatus status, OffsetDateTime now) {

    LessonProgressId id = new LessonProgressId(userId, lessonId);

    LessonProgress lp =
        lessonProgressRepo
            .findById(id)
            .orElseGet(
                () ->
                    LessonProgress.builder()
                        .id(id)
                        .status(LessonProgressStatus.NOT_STARTED)
                        .progressPercentage(0)
                        .computedAt(now)
                        .build());

    lp.setProgressPercentage(clamp0to100(perc));
    lp.setStatus(status);
    lp.setComputedAt(now);

    lessonProgressRepo.save(lp);
    return lp;
  }

  /** Пересчёт CourseProgress: среднее по LessonProgress (по lessonIds курса) */
  private CourseProgress recalcCourse(
      UUID userId, UUID courseId, List<UUID> courseLessonIds, OffsetDateTime now) {

    int total = courseLessonIds.size();
    if (total == 0) {
      return upsertCourse(userId, courseId, 0, "NOT_STARTED", now);
    }

    List<LessonProgress> stored =
        lessonProgressRepo.findByIdUserIdAndIdLessonIdIn(userId, courseLessonIds);

    Map<UUID, LessonProgress> lpByLesson =
        stored.stream()
            .collect(Collectors.toMap(p -> p.getId().getLessonId(), p -> p, (a, b) -> a));

    long sum = 0;
    boolean anyStarted = false;
    boolean allCompleted = true;

    for (UUID lid : courseLessonIds) {
      LessonProgress lp = lpByLesson.get(lid);

      int perc = lp != null ? lp.getProgressPercentage() : 0;
      sum += perc;

      if (perc > 0) {
        anyStarted = true;
      }

      if (lp == null || lp.getStatus() != LessonProgressStatus.COMPLETED) {
        allCompleted = false;
      }
    }

    int avg = clamp0to100((int) Math.round(sum * 1.0 / total));

    String status;
    if (allCompleted) {
      status = "COMPLETED";
    } else if (anyStarted || avg > 0) {
      status = "IN_PROGRESS";
    } else {
      status = "NOT_STARTED";
    }

    return upsertCourse(userId, courseId, avg, status, now);
  }

  private CourseProgress upsertCourse(
      UUID userId, UUID courseId, int perc, String status, OffsetDateTime now) {

    CourseProgressId id = new CourseProgressId(userId, courseId);

    CourseProgress cp =
        courseProgressRepo
            .findById(id)
            .orElseGet(
                () ->
                    CourseProgress.builder()
                        .id(id)
                        .progressPercentage(0)
                        .status("NOT_STARTED")
                        .computedAt(now)
                        .lastActivityAt(now)
                        .build());

    cp.setProgressPercentage(clamp0to100(perc));
    cp.setStatus(status);
    cp.setComputedAt(now);
    cp.setLastActivityAt(now);

    if ("COMPLETED".equals(status) && cp.getCompletedAt() == null) {
      cp.setCompletedAt(now);
    }

    courseProgressRepo.save(cp);
    return cp;
  }

  private static int clamp0to100(int v) {
    return Math.max(0, Math.min(100, v));
  }
}
