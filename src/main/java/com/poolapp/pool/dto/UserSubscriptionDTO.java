package com.poolapp.pool.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscriptionDTO {

    @NotBlank
    @Size(max = 255)
    @Email
    private String userEmail;

    @Valid
    @NotNull
    private SubscriptionDTO subscriptionDTO;

}
