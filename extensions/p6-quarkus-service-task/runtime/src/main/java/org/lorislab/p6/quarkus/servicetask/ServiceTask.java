package org.lorislab.p6.quarkus.servicetask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServiceTask {

    String name() default "";

    Process process() default @Process(id = "", version = "");
}
