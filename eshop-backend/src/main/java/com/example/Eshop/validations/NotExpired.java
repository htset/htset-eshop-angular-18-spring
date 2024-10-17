package com.example.Eshop.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExpiredCardValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotExpired {
  String message() default "The card has expired";
  Class<?>[] groups() default {}; //groups parameter
  Class<? extends Payload>[] payload() default {}; //payload parameter
}