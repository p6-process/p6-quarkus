package org.lorislab.p6.quarkus.servicetask;

import java.util.Map;

public class ServiceTaskInput {

    private String id;

    private String version;

    private String name;

    private Map<String, Object> data;

    public ServiceTaskInput(String id, String version, String name, Map<String, Object> data) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
