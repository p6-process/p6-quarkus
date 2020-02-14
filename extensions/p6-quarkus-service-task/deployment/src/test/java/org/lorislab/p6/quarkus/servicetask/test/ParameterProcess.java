package org.lorislab.p6.quarkus.servicetask.test;

import org.lorislab.p6.quarkus.servicetask.Process;
import org.lorislab.p6.quarkus.servicetask.ServiceTask;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskInput;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Process(id = "parameterProcess", version = "1.0")
public class ParameterProcess {

    private static final Logger log = LoggerFactory.getLogger(ParameterProcess.class);

    @ServiceTask(name = "parameter1")
    public ServiceTaskOutput parameter1(ServiceTaskInput data) {
        ParameterModel input = data.getParameter("input", ParameterModel.class);
        log.info("Execute parameter1. Parameter: {}/{}", input.param1, input.param2);
        ParameterModel output = new ParameterModel();
        output.param1 = input.param1 + "_out1";
        output.param2 = input.param2 + "_out2";
        return ServiceTaskOutput.data("KEY1", output);
    }

}
