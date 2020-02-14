package org.lorislab.p6.quarkus.servicetask.runtime;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskInput;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskOutput;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessExecutor {

    private static final String IGNORE = "";

    public ServiceTaskOutput execute(ServiceTaskInput in) {
        InstanceHandle<ServiceTaskExecutor> w = getServiceTaskExecutor(in.getId(), in.getVersion(), in.getName());
        if (!w.isAvailable()) {
            throw new IllegalStateException("No service task implementation!");
        }
        return w.get().execute(in);
    }

    private InstanceHandle<ServiceTaskExecutor> getServiceTaskExecutor(String id, String version, String name) {
        InstanceHandle<ServiceTaskExecutor> w = Arc.container().instance(ServiceTaskExecutor.class, ServiceTaskId.Literal.create(id, version, name));
        if (!w.isAvailable()) {
            w = Arc.container().instance(ServiceTaskExecutor.class, ServiceTaskId.Literal.create(id, IGNORE, name));
            if (!w.isAvailable()) {
                w = Arc.container().instance(ServiceTaskExecutor.class, ServiceTaskId.Literal.create(id, version, IGNORE));
                if (!w.isAvailable()) {
                    w = Arc.container().instance(ServiceTaskExecutor.class, ServiceTaskId.Literal.create(id, IGNORE, IGNORE));
                    if (!w.isAvailable()) {
                        w = Arc.container().instance(ServiceTaskExecutor.class, ServiceTaskId.Literal.create(IGNORE, IGNORE, IGNORE));
                    }
                }
            }
        }
        return w;
    }
}
