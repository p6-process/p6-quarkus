package org.lorislab.p6.quarkus.servicetask.test;

import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskInput;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskOutput;
import org.lorislab.p6.quarkus.servicetask.runtime.ServiceTaskExecutor;
import org.lorislab.p6.quarkus.servicetask.runtime.ServiceTaskId;

import javax.inject.Inject;
import java.util.Collections;

public class ServiceTaskInjectTest {

    @Inject
    @ServiceTaskId(id = "process1", version = "1.0", name = "service1")
    ServiceTaskExecutor executor;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(SimpleProcess.class)
            );

    @Test
    @DisplayName("Service task test")
    public void testServiceTask() {
        Assertions.assertNotNull(executor);

        ServiceTaskInput input = new ServiceTaskInput("process1", "1.0", "service1"
                , Collections.singletonMap("INPUT", "21"));
        ServiceTaskOutput out = executor.execute(input);
        Assertions.assertNotNull(out);
        Assertions.assertNotNull(out.getData());
        Assertions.assertFalse(out.getData().isEmpty());
        Assertions.assertEquals("VALUE1_21", out.getData().get("KEY1"));
    }
}
