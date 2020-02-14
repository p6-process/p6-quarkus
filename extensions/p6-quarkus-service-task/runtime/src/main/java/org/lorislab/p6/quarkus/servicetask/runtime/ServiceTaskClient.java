package org.lorislab.p6.quarkus.servicetask.runtime;

import io.smallrye.reactive.messaging.amqp.AmqpMessage;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
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

    @Incoming("process-start")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> serviceTask(AmqpMessage<String> message) {
        return CompletableFuture.runAsync(() -> {
            try {
                JsonObject data = message.getApplicationProperties();

                message.getAmqpMessage().accepted();
            } catch (Exception wex) {
                log.error("Error process service task message. Message {}", message);
                message.getAmqpMessage().modified(true, false);
            }
        });
    }

}
