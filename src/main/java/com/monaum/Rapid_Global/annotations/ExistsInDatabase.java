package com.monaum.Rapid_Global.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Documented
@Constraint(validatedBy = ExistsInDatabaseValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsInDatabase {
    String message() default "Value does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<?> entity(); // entity class to check existence for
}
