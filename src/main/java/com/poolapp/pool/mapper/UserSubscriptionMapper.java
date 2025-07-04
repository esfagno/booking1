package com.poolapp.pool.mapper;

import com.poolapp.pool.dto.UserSubscriptionDTO;
import com.poolapp.pool.model.UserSubscription;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SubscriptionTypeMapper.class, SubscriptionMapper.class}, builder = @Builder(disableBuilder = true),
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED)
public interface UserSubscriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "remainingBookings", ignore = true)
    @Mapping(target = "subscription", source = "subscriptionDTO")
    UserSubscription toEntity(UserSubscriptionDTO dto);

    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "subscriptionDTO", source = "subscription")
    UserSubscriptionDTO toDto(UserSubscription userSubscription);

    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "subscriptionDTO", source = "subscription")
    List<UserSubscriptionDTO> toDtoList(List<UserSubscription> userSubscriptions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedAt", ignore = true)
    @Mapping(target = "remainingBookings", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "subscription", source = "subscriptionDTO")
    void updateUserSubscriptionFromDto(@MappingTarget UserSubscription userSubscription, UserSubscriptionDTO userSubscriptionDTO);
}


