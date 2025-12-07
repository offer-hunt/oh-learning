package ru.offer.hunt.learning.service;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.offer.hunt.learning.model.dto.LearningExportDto;
import ru.offer.hunt.learning.model.mapper.CourseProgressMapper;
import ru.offer.hunt.learning.model.mapper.EnrollmentMapper;
import ru.offer.hunt.learning.model.mapper.LearningRatingMapper;
import ru.offer.hunt.learning.model.mapper.LessonProgressMapper;
import ru.offer.hunt.learning.model.mapper.PageProgressMapper;
import ru.offer.hunt.learning.model.mapper.QuestionStateMapper;
import ru.offer.hunt.learning.model.repository.CourseProgressRepository;
import ru.offer.hunt.learning.model.repository.LearningEnrollmentRepository;
import ru.offer.hunt.learning.model.repository.LearningRatingRepository;
import ru.offer.hunt.learning.model.repository.LessonProgressRepository;
import ru.offer.hunt.learning.model.repository.PageProgressRepository;
import ru.offer.hunt.learning.model.repository.QuestionStateRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LearningExportService {

  private final LearningEnrollmentRepository enrollmentRepo;
  private final CourseProgressRepository courseProgressRepo;
  private final LessonProgressRepository lessonProgressRepo;
  private final PageProgressRepository pageProgressRepo;
  private final QuestionStateRepository questionStateRepo;
  private final LearningRatingRepository ratingRepo;

  private final EnrollmentMapper enrollmentMapper;
  private final CourseProgressMapper courseProgressMapper;
  private final LessonProgressMapper lessonProgressMapper;
  private final PageProgressMapper pageProgressMapper;
  private final QuestionStateMapper questionStateMapper;
  private final LearningRatingMapper ratingMapper;

  public LearningExportDto export(UUID userId) {
    LearningExportDto dto = new LearningExportDto();
    dto.setUserId(userId);
    dto.setGeneratedAt(OffsetDateTime.now());

    dto.setEnrollments(
        enrollmentRepo.findByIdUserId(userId).stream().map(enrollmentMapper::toDto).toList());
    dto.setCourseProgress(
        courseProgressRepo.findByIdUserId(userId).stream()
            .map(courseProgressMapper::toDto)
            .toList());
    dto.setLessonProgress(
        lessonProgressRepo.findByIdUserId(userId).stream()
            .map(lessonProgressMapper::toDto)
            .toList());
    dto.setPageProgress(
        pageProgressRepo.findByIdUserId(userId).stream().map(pageProgressMapper::toDto).toList());
    dto.setQuestionStates(
        questionStateRepo.findByIdUserId(userId).stream().map(questionStateMapper::toDto).toList());
    dto.setRatings(ratingRepo.findByIdUserId(userId).stream().map(ratingMapper::toDto).toList());

    return dto;
  }
}
