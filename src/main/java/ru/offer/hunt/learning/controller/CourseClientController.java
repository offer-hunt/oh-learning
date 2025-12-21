package ru.offer.hunt.learning.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.offer.hunt.learning.model.dto.CourseStructureDto;
import ru.offer.hunt.learning.service.CourseServiceClient;

@RestController
@RequestMapping("/api/dev/course")
@RequiredArgsConstructor
@Profile("local") // чтобы наружу не торчало в dev/prod
public class CourseClientController {

  private final CourseServiceClient courseServiceClient;

  // дергает будущий эндпоинт структуры
  @GetMapping("/{courseId}/structure")
  public ResponseEntity<CourseStructureDto> structure(@PathVariable UUID courseId) {
    return ResponseEntity.ok(courseServiceClient.getCourseStructure(courseId));
  }

  // дергает уже существующий batch
  // вызывать так: /api/dev/course/batch?ids=uuid1&ids=uuid2
  @GetMapping("/batch")
  public ResponseEntity<List<CourseStructureDto>> batch(@RequestParam("ids") List<UUID> ids) {
    return ResponseEntity.ok(courseServiceClient.getCoursesBatch(ids));
  }
}
