package ru.offer.hunt.learning.model.mapper;

import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.offer.hunt.learning.model.dto.LearningRatingDto;
import ru.offer.hunt.learning.model.dto.LearningRatingUpsertRequest;
import ru.offer.hunt.learning.model.entity.LearningRating;
import ru.offer.hunt.learning.model.id.RatingId;

@Mapper(
    componentModel = "spring",
    imports = {RatingId.class})
public interface LearningRatingMapper {

  @Mapping(
      target = "userId",
      expression = "java(src.getId()!=null ? src.getId().getUserId() : null)")
  @Mapping(
      target = "courseId",
      expression = "java(src.getId()!=null ? src.getId().getCourseId() : null)")
  LearningRatingDto toDto(LearningRating src);

  @Mapping(target = "id", expression = "java(new RatingId(userId, courseId))")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  LearningRating toEntity(UUID userId, UUID courseId, LearningRatingUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void update(@MappingTarget LearningRating target, LearningRatingUpsertRequest req);
}
