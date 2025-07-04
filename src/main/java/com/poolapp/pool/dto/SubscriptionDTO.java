package com.poolapp.pool.dto;

import com.poolapp.pool.model.enums.SubscriptionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {

    @Valid
    @NotNull
    private SubscriptionTypeDTO subscriptionTypeDTO;

    @NotNull
    private SubscriptionStatus status;

}
