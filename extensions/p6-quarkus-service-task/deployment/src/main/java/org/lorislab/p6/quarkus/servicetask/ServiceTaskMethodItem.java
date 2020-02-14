package org.lorislab.p6.quarkus.servicetask;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;

public class ServiceTaskMethodItem {

    private final ClassInfo clazz;

    private final String id;

    private final String version;

    private final String name;

    private final MethodInfo method;

    public ServiceTaskMethodItem(ClassInfo clazz, MethodInfo method, String id, String version, String name) {
        this.clazz = clazz;
        this.method = method;
        this.id = id;
        this.version = version;
        this.name = name;
    }

    public ClassInfo getClazz() {
        return clazz;
    }

    public MethodInfo getMethod() {
        return method;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
