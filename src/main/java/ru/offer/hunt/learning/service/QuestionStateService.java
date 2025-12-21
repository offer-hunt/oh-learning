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
import ru.offer.hunt.learning.model.dto.InternalUpsertResultRequest;
import ru.offer.hunt.learning.model.dto.QuestionStateDto;
import ru.offer.hunt.learning.model.dto.QuestionStateUpsertRequest;
import ru.offer.hunt.learning.model.entity.CourseProgress;
import ru.offer.hunt.learning.model.entity.LearningEnrollment;
import ru.offer.hunt.learning.model.entity.LessonProgress;
import ru.offer.hunt.learning.model.entity.PageProgress;
import ru.offer.hunt.learning.model.entity.QuestionState;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;
import ru.offer.hunt.learning.model.enums.LessonProgressStatus;
import ru.offer.hunt.learning.model.enums.PageProgressStatus;
import ru.offer.hunt.learning.model.enums.SubmissionStatus;
import ru.offer.hunt.learning.model.id.CourseProgressId;
import ru.offer.hunt.learning.model.id.EnrollmentId;
import ru.offer.hunt.learning.model.id.LessonProgressId;
import ru.offer.hunt.learning.model.id.PageProgressId;
import ru.offer.hunt.learning.model.id.QuestionStateId;
import ru.offer.hunt.learning.model.mapper.QuestionStateMapper;
import ru.offer.hunt.learning.model.repository.CourseProgressRepository;
import ru.offer.hunt.learning.model.repository.LearningEnrollmentRepository;
import ru.offer.hunt.learning.model.repository.LessonProgressRepository;
import ru.offer.hunt.learning.model.repository.PageProgressRepository;
import ru.offer.hunt.learning.model.repository.QuestionStateRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestionStateService {

  private final QuestionStateRepository questionStateRepo;
  private final PageProgressRepository pageProgressRepo;
  private final LessonProgressRepository lessonProgressRepo;
  private final CourseProgressRepository courseProgressRepo;
  private final LearningEnrollmentRepository enrollmentRepo;

  private final QuestionStateMapper mapper;
  private final CourseServiceClient courseServiceClient;

  @Transactional(readOnly = true)
  public QuestionStateDto get(UUID userId, UUID questionId) {
    var id = new QuestionStateId(userId, questionId);
    var entity =
        questionStateRepo
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QuestionState not found"));
    return mapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  public List<QuestionStateDto> listByUser(UUID userId) {
    return questionStateRepo.findByIdUserId(userId).stream().map(mapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<QuestionStateDto> listByQuestion(UUID questionId) {
    return questionStateRepo.findByIdQuestionId(questionId).stream().map(mapper::toDto).toList();
  }

  public QuestionStateDto create(UUID userId, UUID questionId, QuestionStateUpsertRequest req) {
    QuestionState entity = mapper.toEntity(userId, questionId, req);

    if (req.getSolved() == null) {
      entity.setSolved(false);
    }
    if (entity.isSolved() && entity.getSolvedAt() == null) {
      entity.setSolvedAt(OffsetDateTime.now());
    }
    entity.setLastUpdatedAt(OffsetDateTime.now());

    questionStateRepo.save(entity);
    return mapper.toDto(entity);
  }

  public QuestionStateDto update(UUID userId, UUID questionId, QuestionStateUpsertRequest req) {
    var id = new QuestionStateId(userId, questionId);
    var entity =
        questionStateRepo
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QuestionState not found"));

    mapper.update(entity, req);

    if (entity.isSolved() && entity.getSolvedAt() == null) {
      entity.setSolvedAt(OffsetDateTime.now());
    }
    entity.setLastUpdatedAt(OffsetDateTime.now());

    questionStateRepo.save(entity);
    return mapper.toDto(entity);
  }

  public void delete(UUID userId, UUID questionId) {
    var id = new QuestionStateId(userId, questionId);
    if (!questionStateRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "QuestionState not found");
    }
    questionStateRepo.deleteById(id);
  }

  public QuestionStateDto upsertResultFromEvaluation(InternalUpsertResultRequest req) {
    OffsetDateTime now = OffsetDateTime.now();

    UUID userId = req.getUserId();
    UUID questionId = req.getQuestionId();
    SubmissionStatus status = req.getSubmissionStatus();

    // 1) upsert QuestionState
    QuestionStateId id = new QuestionStateId(userId, questionId);
    QuestionState qs =
        questionStateRepo
            .findById(id)
            .orElseGet(
                () -> QuestionState.builder().id(id).solved(false).lastUpdatedAt(now).build());

    qs.setLastUpdatedAt(now);
    qs.setLastStatus(status);

    if (req.getScore() != null) {
      qs.setLastScore(req.getScore());
    }
    if (req.getFeedback() != null) {
      qs.setLastFeedback(req.getFeedback());
    }

    boolean solvedNow = status == SubmissionStatus.ACCEPTED;
    if (solvedNow && !qs.isSolved()) {
      qs.setSolved(true);
      qs.setSolvedAt(now);
    }

    // score/feedback сейчас просто принимаем (можно сохранить позже, когда добавите колонки)
    if (req.getScore() != null || req.getFeedback() != null) {
      log.debug(
          "Evaluation meta received (not persisted yet): userId={}, questionId={}, score={}, feedbackLen={}",
          userId,
          questionId,
          req.getScore(),
          req.getFeedback() != null ? req.getFeedback().length() : 0);
    }

    questionStateRepo.save(qs);

    // 2) находим, где этот questionId находится: courseId/lessonId/pageId (+ списки)
    QuestionLocation loc = resolveQuestionLocationOrThrow(userId, questionId);

    UUID courseId = loc.courseId();
    UUID lessonId = loc.lessonId();
    UUID pageId = loc.pageId();

    // 3) проверяем enrollment по найденному курсу
    LearningEnrollment enrollment =
        enrollmentRepo
            .findById(new EnrollmentId(userId, courseId))
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

    if (enrollment.getStatus() == EnrollmentStatus.REVOKED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Enrollment revoked");
    }

    // 4) пересчитать PageProgress на этой странице (по всем вопросам страницы)
    PageProgress pageProgress =
        recalcPageProgress(userId, pageId, loc.pageQuestionIds(), now, status);

    // 5) пересчитать LessonProgress и CourseProgress (цепочка)
    LessonProgress lessonProgress = recalcLesson(userId, lessonId, loc.lessonPageIds(), now);

    CourseProgress courseProgress = recalcCourse(userId, courseId, loc.courseLessonIds(), now);

    // 6) enrollment last activity + completed
    enrollment.setLastActivityAt(now);
    if (enrollment.getStartedAt() == null) {
      enrollment.setStartedAt(now);
    }

    if ("COMPLETED".equals(courseProgress.getStatus())
        && enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
      enrollment.setStatus(EnrollmentStatus.COMPLETED);
      if (enrollment.getCompletedAt() == null) {
        enrollment.setCompletedAt(now);
      }
    }

    enrollmentRepo.save(enrollment);

    log.info(
        "Upsert-result done: userId={}, courseId={}, lessonId={}, pageId={}, questionId={}, pageStatus={}, lesson%={}, course%={}",
        userId,
        courseId,
        lessonId,
        pageId,
        questionId,
        pageProgress.getStatus(),
        lessonProgress.getProgressPercentage(),
        courseProgress.getProgressPercentage());

    return mapper.toDto(qs);
  }

  /**
   * Ищем курс/урок/страницу, где лежит questionId. Реализация: берём все enrollments пользователя →
   * по каждому дергаем CourseService структуры → ищем вопрос в lessons/pages/questions.
   */
  private QuestionLocation resolveQuestionLocationOrThrow(UUID userId, UUID questionId) {
    List<LearningEnrollment> enrollments = enrollmentRepo.findByIdUserId(userId);
    if (enrollments == null || enrollments.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No enrollments for user");
    }

    for (LearningEnrollment e : enrollments) {
      if (e.getStatus() == EnrollmentStatus.REVOKED) {
        continue;
      }

      UUID courseId = e.getId().getCourseId();

      CourseStructureDto structure;
      try {
        structure = courseServiceClient.getCourseStructure(courseId);
      } catch (Exception ex) {
        log.warn(
            "Course structure fetch failed for scanning: courseId={}, userId={}",
            courseId,
            userId,
            ex);
        continue; // пробуем следующий курс
      }

      if (structure == null || structure.lessons() == null) {
        continue;
      }

      List<UUID> courseLessonIds =
          structure.lessons().stream().map(CourseStructureDto.LessonDto::lessonId).toList();

      for (var lesson : structure.lessons()) {
        if (lesson.pages() == null) {
          continue;
        }

        List<UUID> lessonPageIds =
            lesson.pages().stream().map(CourseStructureDto.PageDto::pageId).toList();

        for (var page : lesson.pages()) {
          if (page.questions() == null) {
            continue;
          }

          boolean contains =
              page.questions().stream().anyMatch(q -> questionId.equals(q.questionId()));

          if (contains) {
            List<UUID> pageQuestionIds =
                page.questions().stream().map(CourseStructureDto.QuestionDto::questionId).toList();

            return new QuestionLocation(
                courseId,
                lesson.lessonId(),
                page.pageId(),
                courseLessonIds,
                lessonPageIds,
                pageQuestionIds);
          }
        }
      }
    }

    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found in user courses");
  }

  private record QuestionLocation(
      UUID courseId,
      UUID lessonId,
      UUID pageId,
      List<UUID> courseLessonIds,
      List<UUID> lessonPageIds,
      List<UUID> pageQuestionIds) {}

  /**
   * PageProgress считается по вопросам страницы: все solved -> COMPLETED, иначе
   * IN_PROGRESS/NOT_STARTED
   */
  private PageProgress recalcPageProgress(
      UUID userId,
      UUID pageId,
      List<UUID> pageQuestionIds,
      OffsetDateTime now,
      SubmissionStatus incomingStatus) {

    PageProgressId ppId = new PageProgressId(userId, pageId);
    PageProgress pp =
        pageProgressRepo
            .findById(ppId)
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

    PageProgressStatus newStatus;

    if (pageQuestionIds == null || pageQuestionIds.isEmpty()) {
      // странно, но пусть не падает
      newStatus = PageProgressStatus.IN_PROGRESS;
    } else {
      List<QuestionState> stored =
          questionStateRepo.findByIdUserIdAndIdQuestionIdIn(userId, pageQuestionIds);

      Map<UUID, QuestionState> byQ =
          stored.stream()
              .collect(Collectors.toMap(q -> q.getId().getQuestionId(), q -> q, (a, b) -> a));

      long solved =
          pageQuestionIds.stream().map(byQ::get).filter(q -> q != null && q.isSolved()).count();

      if (solved == pageQuestionIds.size()) {
        newStatus = PageProgressStatus.COMPLETED;
      } else {
        // если пришёл REJECTED/ACCEPTED/PENDING — считаем что страница начата
        boolean started = solved > 0 || incomingStatus != null;
        newStatus = started ? PageProgressStatus.IN_PROGRESS : PageProgressStatus.NOT_STARTED;
      }
    }

    pp.setStatus(newStatus);

    if (newStatus == PageProgressStatus.COMPLETED && pp.getCompletedAt() == null) {
      pp.setCompletedAt(now);
    }

    if (pp.getTimeSpentSec() < 0) {
      pp.setTimeSpentSec(0);
    }
    if (pp.getAttemptsCount() < 0) {
      pp.setAttemptsCount(0);
    }

    pageProgressRepo.save(pp);
    return pp;
  }

  /** LessonProgress: % = completedPages / totalPages */
  private LessonProgress recalcLesson(
      UUID userId, UUID lessonId, List<UUID> lessonPageIds, OffsetDateTime now) {

    int total = lessonPageIds != null ? lessonPageIds.size() : 0;
    if (total == 0) {
      return upsertLesson(userId, lessonId, 0, LessonProgressStatus.NOT_STARTED, now);
    }

    List<PageProgress> stored = pageProgressRepo.findByIdUserIdAndIdPageIdIn(userId, lessonPageIds);

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

  /** CourseProgress: среднее по урокам */
  private CourseProgress recalcCourse(
      UUID userId, UUID courseId, List<UUID> courseLessonIds, OffsetDateTime now) {

    int total = courseLessonIds != null ? courseLessonIds.size() : 0;
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
