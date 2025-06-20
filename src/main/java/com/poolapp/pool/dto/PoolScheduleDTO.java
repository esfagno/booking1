package com.poolapp.pool.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoolScheduleDTO {

    private Integer id;

    private Integer poolId;

    @NotNull
    @Min(1)
    @Max(7)
    private Short dayOfWeek;

    @NotNull
    private LocalTime openingTime;

    @NotNull
    private LocalTime closingTime;

    @AssertTrue
    public boolean isOpeningBeforeClosing() {
        if (openingTime == null || closingTime == null) {
            return true;
        }
        return openingTime.isBefore(closingTime);
    }
}
