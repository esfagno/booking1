package com.poolapp.pool.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionTypeDTO {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Min(0)
    private Integer maxBookingsPerMonth;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal price;

    @NotNull
    @Min(1)
    private Integer durationDays;

    @Size(max = 255)
    private String description;
}
