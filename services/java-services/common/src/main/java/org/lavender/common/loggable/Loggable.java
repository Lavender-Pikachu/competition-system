package org.lavender.common.loggable;

import org.lavender.common.enums.OperatorType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface Loggable {
    String title() default "";
    String value() default "";
    OperatorType operatorType() default OperatorType.ADMIN;
    boolean recordRequest() default true;
    boolean recordResponse() default true;
    boolean maskSensitiveData() default true;
}
