package org.lorislab.p6.quarkus.servicetask.runtime;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Qualifier
@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface ServiceTaskId {

    String id();

    String version();

    String name();

    final class Literal extends AnnotationLiteral<ServiceTaskId> implements ServiceTaskId {

        public static Literal create(String id, String version, String name) {
            return new Literal(id, version, name);
        }

        private static final long serialVersionUID = 1L;

        private String id;

        private String version;

        private String name;

        public Literal(String id, String version, String name) {
            this.id = id;
            this.version = version;
            this.name = name;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String version() {
            return version;
        }
    }
}