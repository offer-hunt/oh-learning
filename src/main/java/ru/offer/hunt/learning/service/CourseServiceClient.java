package ru.offer.hunt.learning.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.offer.hunt.learning.model.dto.CourseStructureDto;
import ru.offer.hunt.learning.security.SecurityUtils;

@Component
@RequiredArgsConstructor
public class CourseServiceClient {

  private final RestClient courseRestClient;

  public CourseStructureDto getCourseStructure(UUID courseId) {
    return courseRestClient
        .get()
        .uri("/api/v1/courses/{id}/structure", courseId)
        .headers(this::addAuth)
        .retrieve()
        .body(CourseStructureDto.class);
  }

  public List<CourseStructureDto> getCoursesBatch(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }

    return courseRestClient
        .get()
        .uri(uri -> uri.path("/api/courses/batch").queryParam("ids", ids).build())
        .headers(this::addAuth)
        .retrieve()
        .body(new ParameterizedTypeReference<List<CourseStructureDto>>() {});
  }

  private void addAuth(HttpHeaders headers) {
    String token = SecurityUtils.getBearerTokenOrNull();
    if (token != null && !token.isBlank()) {
      headers.setBearerAuth(token);
    }
  }
}
