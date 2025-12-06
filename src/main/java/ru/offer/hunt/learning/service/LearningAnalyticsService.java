package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.offer.hunt.learning.model.dto.CourseAnalyticsDto;
import ru.offer.hunt.learning.model.entity.CourseProgress;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;
import ru.offer.hunt.learning.model.repository.CourseProgressRepository;
import ru.offer.hunt.learning.model.repository.LearningEnrollmentRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LearningAnalyticsService {

  private final LearningEnrollmentRepository enrollmentRepo;
  private final CourseProgressRepository courseProgressRepo;

  public CourseAnalyticsDto analyticsForCourse(UUID courseId) {
    return build(courseId);
  }

  public List<CourseAnalyticsDto> analyticsForCourses(List<UUID> courseIds) {
    if (courseIds == null || courseIds.isEmpty()) {
      return List.of();
    }

    Map<UUID, List<CourseProgress>> progressByCourse =
        courseProgressRepo.findByIdCourseIdIn(courseIds).stream()
            .collect(Collectors.groupingBy(p -> p.getId().getCourseId()));

    OffsetDateTime now = OffsetDateTime.now();
    OffsetDateTime weekAgo = now.minusDays(7);

    List<CourseAnalyticsDto> out = new ArrayList<>();
    for (UUID courseId : courseIds) {
      long total = enrollmentRepo.countByIdCourseIdAndStatusNot(courseId, EnrollmentStatus.REVOKED);
      long completed =
          enrollmentRepo.countByIdCourseIdAndStatus(courseId, EnrollmentStatus.COMPLETED);
      long active =
          enrollmentRepo.countByIdCourseIdAndLastActivityAtAfterAndStatusNot(
              courseId, weekAgo, EnrollmentStatus.REVOKED);

      int completionPercent = total == 0 ? 0 : (int) Math.round(completed * 100.0 / total);

      List<CourseProgress> cps = progressByCourse.getOrDefault(courseId, List.of());
      int avgProgress =
          cps.isEmpty()
              ? 0
              : (int)
                  Math.round(
                      cps.stream()
                          .mapToInt(CourseProgress::getProgressPercentage)
                          .average()
                          .orElse(0));

      CourseAnalyticsDto dto = new CourseAnalyticsDto();
      dto.setCourseId(courseId);
      dto.setStudentsTotal(total);
      dto.setStudentsCompleted(completed);
      dto.setStudentsActiveLastWeek(active);
      dto.setCompletionPercent(completionPercent);
      dto.setAvgProgressPercent(clamp0to100(avgProgress));
      dto.setCalculatedAt(now);
      out.add(dto);
    }

    return out;
  }

  private CourseAnalyticsDto build(UUID courseId) {
    return analyticsForCourses(List.of(courseId)).stream()
        .findFirst()
        .orElseGet(
            () -> {
              CourseAnalyticsDto dto = new CourseAnalyticsDto();
              dto.setCourseId(courseId);
              dto.setCalculatedAt(OffsetDateTime.now());
              return dto;
            });
  }

  private static int clamp0to100(int v) {
    return Math.max(0, Math.min(100, v));
  }
}
