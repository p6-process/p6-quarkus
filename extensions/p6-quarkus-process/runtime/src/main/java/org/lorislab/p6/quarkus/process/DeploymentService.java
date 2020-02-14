package org.lorislab.p6.quarkus.process;

import io.smallrye.reactive.messaging.amqp.AmqpMessage;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class DeploymentService {

    @Inject
    @Channel("p6-deployment")
    Emitter<AmqpMessage<String>> emitter;

    public String deploy(String process) {
        return deploy(process, UUID.randomUUID().toString());
    }

    public String deploy(String process, String deploymentId) {
        AmqpMessage<String> output = new AmqpMessage<>(io.vertx.amqp.AmqpMessage.create()
                .withBody(process)
                .id(UUID.randomUUID().toString())
                .correlationId(deploymentId)
                .build());
        emitter.send(output);
        return deploymentId;
    }
}
