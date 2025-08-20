package com.zb.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

// import javax.validation.Constraint;
// import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositionValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPosition {
    String message() default "职位不能包含'经理'或'管理'字样";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}