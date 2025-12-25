package com.vijay.User_Master.annotation;

import com.vijay.User_Master.entity.EMRAuditLog.EntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that should be audited.
 * Use on service methods that modify EMR data.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
    /**
     * The type of entity being audited
     */
    EntityType entityType();
}
