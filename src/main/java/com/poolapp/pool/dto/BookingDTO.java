package com.poolapp.pool.dto;

import com.poolapp.pool.model.enums.BookingStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {

    @NotBlank
    @Size(max = 255)
    @Email
    private String userEmail;

    @NotNull
    private LocalDateTime bookingTime;

    @NotNull
    private BookingStatus status;

}
