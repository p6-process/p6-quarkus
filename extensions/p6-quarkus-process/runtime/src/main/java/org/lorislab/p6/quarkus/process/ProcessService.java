package org.lorislab.p6.quarkus.process;

import io.smallrye.reactive.messaging.amqp.AmqpMessage;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class ProcessService {

    @Inject
    @Channel("p6-process-start")
    Emitter<AmqpMessage<String>> emitter;

    public String startProcess(String id, String version) {
        return startProcess(id, version, UUID.randomUUID().toString(), null);
    }

    public String startProcess(String id, String version, Map<String, Object> data) {
        return startProcess(id, version, UUID.randomUUID().toString(), data);
    }

    public String startProcess(String id, String version, String instanceId, Map<String, Object> data) {
        Map<String, Object> header = new HashMap<>();
        data.put("processId", id);
        data.put("processInstanceId", instanceId);
        data.put("processVersion", version);

        AmqpMessage<String> output = new AmqpMessage<>(io.vertx.amqp.AmqpMessage.create()
                .applicationProperties(JsonObject.mapFrom(header))
                .withBody(JsonObject.mapFrom(data).toString())
                .id(UUID.randomUUID().toString())
                .correlationId(instanceId)
                .build());

        emitter.send(output);

        return instanceId;
    }

}
