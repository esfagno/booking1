package com.poolapp.pool.mapper;

import com.poolapp.pool.dto.BookingDTO;
import com.poolapp.pool.model.Booking;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true),
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED)
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "session", ignore = true)
    Booking toEntity(BookingDTO dto);

    @Mapping(target = "userEmail", source = "user.email")
    BookingDTO toDto(Booking booking);

    @Mapping(target = "userEmail", source = "user.email")
    List<BookingDTO> toDtoList(List<Booking> bookings);
}
