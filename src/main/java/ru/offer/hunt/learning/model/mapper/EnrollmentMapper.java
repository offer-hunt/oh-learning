package ru.offer.hunt.learning.model.mapper;

import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.offer.hunt.learning.model.dto.EnrollmentDto;
import ru.offer.hunt.learning.model.dto.EnrollmentUpsertRequest;
import ru.offer.hunt.learning.model.entity.LearningEnrollment;
import ru.offer.hunt.learning.model.id.EnrollmentId;

@Mapper(
    componentModel = "spring",
    imports = {EnrollmentId.class})
public interface EnrollmentMapper {

  @Mapping(
      target = "userId",
      expression = "java(src.getId()!=null ? src.getId().getUserId() : null)")
  @Mapping(
      target = "courseId",
      expression = "java(src.getId()!=null ? src.getId().getCourseId() : null)")
  EnrollmentDto toDto(LearningEnrollment src);

  @Mapping(target = "id", expression = "java(new EnrollmentId(userId, courseId))")
  LearningEnrollment toEntity(UUID userId, UUID courseId, EnrollmentUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  void update(@MappingTarget LearningEnrollment target, EnrollmentUpsertRequest req);
}
