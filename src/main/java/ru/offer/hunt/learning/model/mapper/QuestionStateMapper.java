package ru.offer.hunt.learning.model.mapper;

import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.offer.hunt.learning.model.dto.QuestionStateDto;
import ru.offer.hunt.learning.model.dto.QuestionStateUpsertRequest;
import ru.offer.hunt.learning.model.entity.QuestionState;
import ru.offer.hunt.learning.model.id.QuestionStateId;

@Mapper(
    componentModel = "spring",
    imports = {QuestionStateId.class})
public interface QuestionStateMapper {

  @Mapping(
      target = "userId",
      expression = "java(src.getId()!=null ? src.getId().getUserId() : null)")
  @Mapping(
      target = "questionId",
      expression = "java(src.getId()!=null ? src.getId().getQuestionId() : null)")
  @Mapping(target = "lastScore", source = "lastScore")
  @Mapping(target = "lastFeedback", source = "lastFeedback")
  QuestionStateDto toDto(QuestionState src);

  @Mapping(target = "id", expression = "java(new QuestionStateId(userId, questionId))")
  @Mapping(target = "lastScore", ignore = true)
  @Mapping(target = "lastFeedback", ignore = true)
  QuestionState toEntity(UUID userId, UUID questionId, QuestionStateUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "lastScore", ignore = true)
  @Mapping(target = "lastFeedback", ignore = true)
  void update(@MappingTarget QuestionState target, QuestionStateUpsertRequest req);
}
