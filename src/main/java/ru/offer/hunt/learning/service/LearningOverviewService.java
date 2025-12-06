package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.offer.hunt.learning.model.dto.CourseProgressDto;
import ru.offer.hunt.learning.model.entity.CourseProgress;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;
import ru.offer.hunt.learning.model.enums.LessonProgressStatus;
import ru.offer.hunt.learning.model.repository.CourseProgressRepository;
import ru.offer.hunt.learning.model.repository.LearningEnrollmentRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LearningOverviewService {

  private final LearningEnrollmentRepository enrollmentRepo;
  private final CourseProgressRepository courseProgressRepo;

  public List<CourseProgressDto> listMyCourses(UUID userId, EnrollmentStatus filterStatus) {
    log.info("Listing my courses, userId={}, filterStatus={}", userId, filterStatus);

    var enrollments = enrollmentRepo.findByIdUserId(userId);

    if (filterStatus != null) {
      enrollments = enrollments.stream().filter(e -> e.getStatus() == filterStatus).toList();
    } else {
      enrollments =
          enrollments.stream().filter(e -> e.getStatus() != EnrollmentStatus.REVOKED).toList();
    }

    var progresses = courseProgressRepo.findByIdUserId(userId);
    Map<UUID, CourseProgress> progressByCourse =
        progresses.stream()
            .collect(
                Collectors.toMap(
                    p -> p.getId().getCourseId(),
                    Function.identity(),
                    (a, b) -> a // если вдруг дубликат по courseId — оставляем первый
                    ));

    var result =
        enrollments.stream()
            .map(
                e -> {
                  var p = progressByCourse.get(e.getId().getCourseId());

                  CourseProgressDto dto = new CourseProgressDto();
                  dto.setCourseId(e.getId().getCourseId());
                  dto.setEnrollmentStatus(e.getStatus());
                  dto.setSource(e.getSource());

                  dto.setEnrolledAt(e.getEnrolledAt());
                  dto.setCompletedAt(e.getCompletedAt());
                  dto.setRevokedAt(e.getRevokedAt());

                  OffsetDateTime last =
                      (p != null && p.getLastActivityAt() != null)
                          ? p.getLastActivityAt()
                          : e.getLastActivityAt();
                  dto.setLastActivityAt(last);

                  if (p != null) {
                    dto.setProgressPercentage(p.getProgressPercentage());
                    dto.setProgressStatus(p.getStatus());
                  }

                  // --- вычисляем статус для UI через уже существующий enum ---
                  LessonProgressStatus computed;
                  if (e.getStatus() == EnrollmentStatus.COMPLETED) {
                    computed = LessonProgressStatus.COMPLETED;
                  } else {
                    Integer perc = (p != null ? p.getProgressPercentage() : null);
                    if (perc != null && perc > 0) {
                      computed = LessonProgressStatus.IN_PROGRESS;
                    } else if (last != null) {
                      // были какие-то действия, но процент не посчитан
                      computed = LessonProgressStatus.IN_PROGRESS;
                    } else {
                      computed = LessonProgressStatus.NOT_STARTED;
                    }
                  }

                  dto.setComputedStatus(computed);

                  return dto;
                })
            .sorted(
                Comparator.comparing(
                        CourseProgressDto::getLastActivityAt,
                        Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(
                        CourseProgressDto::getEnrolledAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();

    log.info("My courses result, userId={}, count={}", userId, result.size());

    return result;
  }
}
