package com.poolapp.pool.dto.validation;

import com.poolapp.pool.dto.SessionDTO;
import com.poolapp.pool.validation.EndTimeAfterStartTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EndTimeAfterStartTimeValidator implements ConstraintValidator<EndTimeAfterStartTime, SessionDTO> {

    @Override
    public boolean isValid(SessionDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            return true;
        }
        return dto.getEndTime().isAfter(dto.getStartTime());
    }
}
