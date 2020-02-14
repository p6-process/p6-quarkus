package org.lorislab.p6.quarkus.servicetask;

import io.vertx.core.json.JsonObject;

import java.util.Map;

public class ServiceTaskInput {

    private final String id;

    private final String version;

    private final String name;

    private final JsonObject parameters;

    public ServiceTaskInput(String id, String version, String name, Map<String, Object> parameters) {
        this(id, version, name, new JsonObject(parameters));
    }

    public ServiceTaskInput(String id, String version, String name, JsonObject parameters) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.parameters = parameters;
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

    public Map<String, Object> getParameters() {
        return parameters.getMap();
    }

    public String getString(String key) {
        return parameters.getString(key);
    }

    public Double getDouble(String key) {
        return parameters.getDouble(key);
    }

    public Long getLong(String key) {
        return parameters.getLong(key);
    }

    public Boolean getBoolean(String key) {
        return parameters.getBoolean(key);
    }

    public Float getFloat(String key) {
        return parameters.getFloat(key);
    }

    public <T> T getParameter(String key, Class<T> clazz) {
        JsonObject obj = parameters.getJsonObject(key);
        if (obj != null) {
            return obj.mapTo(clazz);
        }
        return null;
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }
}
