package org.lorislab.p6.quarkus.servicetask.runtime;

import org.lorislab.p6.quarkus.servicetask.ServiceTaskInput;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskOutput;

public interface ServiceTaskExecutor {

    ServiceTaskOutput execute(ServiceTaskInput param);

}
