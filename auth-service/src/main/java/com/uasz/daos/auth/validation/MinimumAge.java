package com.uasz.daos.auth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinimumAgeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinimumAge {
    String message() default "L'âge minimum requis n'est pas atteint";
    int value() default 18;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}