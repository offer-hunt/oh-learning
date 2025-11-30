package ru.offer.hunt.learning.model.mapper;

import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.offer.hunt.learning.model.dto.LessonProgressDto;
import ru.offer.hunt.learning.model.dto.LessonProgressUpsertRequest;
import ru.offer.hunt.learning.model.entity.LessonProgress;
import ru.offer.hunt.learning.model.id.LessonProgressId;

@Mapper(
    componentModel = "spring",
    imports = {LessonProgressId.class})
public interface LessonProgressMapper {

  @Mapping(
      target = "userId",
      expression = "java(src.getId()!=null ? src.getId().getUserId() : null)")
  @Mapping(
      target = "lessonId",
      expression = "java(src.getId()!=null ? src.getId().getLessonId() : null)")
  LessonProgressDto toDto(LessonProgress src);

  @Mapping(target = "id", expression = "java(new LessonProgressId(userId, lessonId))")
  LessonProgress toEntity(UUID userId, UUID lessonId, LessonProgressUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  void update(@MappingTarget LessonProgress target, LessonProgressUpsertRequest req);
}
