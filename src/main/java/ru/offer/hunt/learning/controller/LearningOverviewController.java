package ru.offer.hunt.learning.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.offer.hunt.learning.model.dto.CourseDetailsRequest;
import ru.offer.hunt.learning.model.dto.CourseProgressDetailsDto;
import ru.offer.hunt.learning.model.dto.CourseProgressDto;
import ru.offer.hunt.learning.model.enums.EnrollmentStatus;
import ru.offer.hunt.learning.security.SecurityUtils;
import ru.offer.hunt.learning.service.CourseDetailsService;
import ru.offer.hunt.learning.service.LearningOverviewService;

@RestController
@RequestMapping("/api/learning/overview")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Validated
@Tag(name = "My Learning", description = "Мои курсы и прогресс")
public class LearningOverviewController {

  private final LearningOverviewService learningOverviewService;
  private final CourseDetailsService courseDetailsService;

  @GetMapping("/courses")
  public List<CourseProgressDto> myCourses(
      @RequestParam(required = false) EnrollmentStatus status, Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return learningOverviewService.listMyCourses(userId, status);
  }

  @PostMapping("/courses/{courseId}/details")
  public CourseProgressDetailsDto courseDetails(
      @PathVariable UUID courseId,
      @RequestBody(required = false) @Valid CourseDetailsRequest req,
      Authentication authentication) {
    UUID userId = SecurityUtils.getUserId(authentication);
    return courseDetailsService.details(userId, courseId, req);
  }
}
