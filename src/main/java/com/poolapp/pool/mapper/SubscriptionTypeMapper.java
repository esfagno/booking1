package com.poolapp.pool.mapper;

import com.poolapp.pool.dto.SubscriptionTypeDTO;
import com.poolapp.pool.model.SubscriptionType;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true),
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED)
public interface SubscriptionTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SubscriptionType toEntity(SubscriptionTypeDTO dto);

    SubscriptionTypeDTO toDto(SubscriptionType subscriptionType);

    List<SubscriptionTypeDTO> toDtoList(List<SubscriptionType> subscriptionTypes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateSubscriptionTypeFromDto(@MappingTarget SubscriptionType subscriptionType, SubscriptionTypeDTO subscriptionTypeDTO);
}
