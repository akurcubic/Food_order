package com.raf.foodOrder.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckSecurity {

    String[] permissions() default {};
    String message() default "You don't have sufficient permissions";
}
