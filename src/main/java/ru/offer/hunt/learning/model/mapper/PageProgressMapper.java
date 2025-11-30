package ru.offer.hunt.learning.model.mapper;

import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.offer.hunt.learning.model.dto.PageProgressDto;
import ru.offer.hunt.learning.model.dto.PageProgressUpsertRequest;
import ru.offer.hunt.learning.model.entity.PageProgress;
import ru.offer.hunt.learning.model.id.PageProgressId;

@Mapper(
    componentModel = "spring",
    imports = {PageProgressId.class})
public interface PageProgressMapper {

  @Mapping(
      target = "userId",
      expression = "java(src.getId()!=null ? src.getId().getUserId() : null)")
  @Mapping(
      target = "pageId",
      expression = "java(src.getId()!=null ? src.getId().getPageId() : null)")
  PageProgressDto toDto(PageProgress src);

  @Mapping(target = "id", expression = "java(new PageProgressId(userId, pageId))")
  PageProgress toEntity(UUID userId, UUID pageId, PageProgressUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  void update(@MappingTarget PageProgress target, PageProgressUpsertRequest req);
}
