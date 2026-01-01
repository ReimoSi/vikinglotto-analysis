package ee.reimosi.lotto.draw;

import ee.reimosi.lotto.draw.dto.DrawRequest;
import ee.reimosi.lotto.draw.dto.DrawResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DrawMapper {
    @Mapping(target = "id", ignore = true)
    Draw toEntity(DrawRequest request);
    DrawResponse toResponse(Draw entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(DrawRequest request, @MappingTarget Draw entity);
}
