package org.lorislab.p6.quarkus.servicetask.test;

import org.lorislab.p6.quarkus.servicetask.ServiceTaskInput;
import org.lorislab.p6.quarkus.servicetask.Process;
import org.lorislab.p6.quarkus.servicetask.ServiceTask;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskOutput;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Process(id = "process1", version = "1.0")
public class SimpleProcess {

    @ServiceTask(name = "service1")
    public ServiceTaskOutput serviceTask1(ServiceTaskInput data) {
        String input = (String) data.getData().get("INPUT");
        return ServiceTaskOutput.data("KEY1", "VALUE1_" + input);
    }

}
