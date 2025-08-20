package com.zb.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// import javax.validation.ConstraintValidator;
// import javax.validation.ConstraintValidatorContext;

public class PositionValidator implements ConstraintValidator<ValidPosition, String> {

    @Override
    public boolean isValid(String position, ConstraintValidatorContext context) {
        if (position == null) {
            return true; // 由@NotBlank处理空值
        }
        return !position.contains("经理") && !position.contains("管理");
    }
}