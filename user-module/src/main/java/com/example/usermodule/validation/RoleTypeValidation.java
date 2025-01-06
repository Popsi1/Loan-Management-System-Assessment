package com.example.usermodule.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Documented
@Constraint(validatedBy = {RoleTypeValidator.class})
@Target({FIELD})
@NotBlank(message = "Role must not be blank")
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleTypeValidation {

    String message() default "Invalid role type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
