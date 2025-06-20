package com.poolapp.pool.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoolDTO {

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private String description;

    @NotNull
    @Min(1)
    private Integer maxCapacity;

    @NotNull
    @Min(10)
    @Max(480)
    private Integer sessionDurationMinutes;
}
