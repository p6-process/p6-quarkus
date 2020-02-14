package org.lorislab.p6.quarkus.servicetask.runtime;

import io.smallrye.reactive.messaging.amqp.AmqpMessage;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.vertx.amqp.AmqpMessageBuilder;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskInput;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ServiceTaskClient {

    private static final Logger log = LoggerFactory.getLogger(ServiceTaskClient.class);

    @Inject
    ProcessExecutor executor;

    @Inject
    @Channel("token-execute-out")
    Emitter<AmqpMessage<String>> emitter;

    @Incoming("service-task")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> serviceTask(AmqpMessage<String> message) {
        return CompletableFuture.runAsync(() -> {
            try {
                ServiceTaskInput input = toServiceTaskInput(message);

                ServiceTaskOutput output = executor.execute(input);

                AmqpMessageBuilder builder = createResponse(message);
                builder.withBody(JsonObject.mapFrom(output).toString());
                emitter.send(new AmqpMessage<>(builder.build()));

                message.getAmqpMessage().accepted();
            } catch (Exception wex) {
                log.error("Error process service task message. Message: {}", message);
                message.getAmqpMessage().modified(true, false);
            }
        });
    }

    static AmqpMessageBuilder createResponse(AmqpMessage<String> message) {
        return io.vertx.amqp.AmqpMessage.create()
                .id(message.getAmqpMessage().id())
                .applicationProperties(message.getApplicationProperties())
                .correlationId(message.getAmqpMessage().correlationId());
    }

    static ServiceTaskInput toServiceTaskInput(AmqpMessage<String> message) {
        JsonObject json = message.getApplicationProperties();
        String processId = json.getString("processId");
        String processVersion = json.getString("processVersion");
        String name = json.getString("name");
        return new ServiceTaskInput(processId, processVersion, name, new JsonObject(message.getPayload()));
    }

}
