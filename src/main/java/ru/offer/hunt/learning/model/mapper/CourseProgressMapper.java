package ru.offer.hunt.learning.model.mapper;

import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.offer.hunt.learning.model.dto.CourseProgressDto;
import ru.offer.hunt.learning.model.dto.CourseProgressUpsertRequest;
import ru.offer.hunt.learning.model.entity.CourseProgress;
import ru.offer.hunt.learning.model.id.CourseProgressId;

@Mapper(
    componentModel = "spring",
    imports = {CourseProgressId.class})
public interface CourseProgressMapper {

  @Mapping(
      target = "courseId",
      expression = "java(src.getId()!=null ? src.getId().getCourseId() : null)")
  @Mapping(target = "progressStatus", source = "status")
  @Mapping(target = "enrollmentStatus", ignore = true)
  @Mapping(target = "source", ignore = true)
  @Mapping(target = "enrolledAt", ignore = true)
  @Mapping(target = "revokedAt", ignore = true)
  @Mapping(target = "computedStatus", ignore = true)
  CourseProgressDto toDto(CourseProgress src);

  @Mapping(target = "id", expression = "java(new CourseProgressId(userId, courseId))")
  CourseProgress toEntity(UUID userId, UUID courseId, CourseProgressUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  void update(@MappingTarget CourseProgress target, CourseProgressUpsertRequest req);
}
