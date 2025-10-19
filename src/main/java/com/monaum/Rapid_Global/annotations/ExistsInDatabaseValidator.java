package com.monaum.Rapid_Global.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

public class ExistsInDatabaseValidator implements ConstraintValidator<ExistsInDatabase, Long> {

    @Autowired
    private ApplicationContext applicationContext;

    private Class<?> entity;

    @Override
    public void initialize(ExistsInDatabase constraintAnnotation) {
        this.entity = constraintAnnotation.entity();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) return true;
        JpaRepository<?, Long> repo = (JpaRepository<?, Long>) applicationContext.getBean(entity.getSimpleName() + "Repo");
        return repo.existsById(value);
    }
}
