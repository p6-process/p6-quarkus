package org.lorislab.p6.quarkus.servicetask.test;

import org.lorislab.p6.quarkus.servicetask.ServiceTaskInput;
import org.lorislab.p6.quarkus.servicetask.Process;
import org.lorislab.p6.quarkus.servicetask.ServiceTask;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Process(id = "process1", version = "1.0")
public class SimpleProcess {

    private static final Logger log = LoggerFactory.getLogger(SimpleProcess.class);

    @ServiceTask(name = "service1")
    public ServiceTaskOutput serviceTask1(ServiceTaskInput data) {
        String input = data.getString("INPUT");
        log.info("Execute service1. Parameter: {}", input);
        return ServiceTaskOutput.data("KEY1", "VALUE1_" + input);
    }

}
