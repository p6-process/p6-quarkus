package org.lorislab.p6.quarkus.servicetask;

import java.util.HashMap;
import java.util.Map;

public class ServiceTaskOutput {

    private Map<String, Object> data = new HashMap<>();

    public Map<String, Object> getData() {
        return data;
    }

    public ServiceTaskOutput add(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public ServiceTaskOutput add(Map<String, Object> data) {
        if (data != null) {
            this.data.putAll(data);
        }
        return this;
    }

    public static ServiceTaskOutput data() {
         return new ServiceTaskOutput();
    }

    public static ServiceTaskOutput data(String key, Object value) {
        return data().add(key, value);
    }

    public static ServiceTaskOutput data(Map<String, Object> data) {
        return data().add(data);
    }
}
