package com.uasz.daos.auth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class MinimumAgeValidator implements ConstraintValidator<MinimumAge, LocalDate> {

    private int minimumAge;

    @Override
    public void initialize(MinimumAge constraintAnnotation) {
        this.minimumAge = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate dateNaissance, ConstraintValidatorContext context) {
        if (dateNaissance == null) {
            return true; // La validation null est gérée par @NotNull
        }

        LocalDate today = LocalDate.now();
        Period period = Period.between(dateNaissance, today);
        int age = period.getYears();

        return age >= minimumAge;
    }
}