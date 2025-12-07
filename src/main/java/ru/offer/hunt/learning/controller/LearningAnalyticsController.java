package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.offer.hunt.learning.model.dto.CourseAnalyticsDto;
import ru.offer.hunt.learning.model.dto.CoursesAnalyticsRequest;
import ru.offer.hunt.learning.service.LearningAnalyticsService;

@RestController
@RequestMapping("/api/learning/analytics")
@RequiredArgsConstructor
@Validated
@Tag(name = "Analytics", description = "Аналитика по курсам (для авторов/кабинета)")
@SecurityRequirement(name = "bearerAuth")
public class LearningAnalyticsController {

  private final LearningAnalyticsService service;

  @GetMapping("/courses/{courseId}")
  @Operation(
      summary = "Аналитика по одному курсу",
      description =
          "Возвращает агрегированную статистику по курсу: общее число студентов, "
              + "число активных за неделю, число завершивших, процент завершения, "
              + "средний процент прогресса. Используется в аналитике кабинета / авторском интерфейсе.")
  public CourseAnalyticsDto course(@PathVariable UUID courseId) {
    return service.analyticsForCourse(courseId);
  }

  @PostMapping("/courses")
  @Operation(
      summary = "Аналитика по нескольким курсам",
      description =
          "Возвращает список аналитических метрик по нескольким курсам. "
              + "Список courseIds передаётся в теле запроса (CoursesAnalyticsRequest). "
              + "Удобно для построения сводных дашбордов.")
  public List<CourseAnalyticsDto> courses(@RequestBody CoursesAnalyticsRequest req) {
    return service.analyticsForCourses(req != null ? req.getCourseIds() : List.of());
  }
}
