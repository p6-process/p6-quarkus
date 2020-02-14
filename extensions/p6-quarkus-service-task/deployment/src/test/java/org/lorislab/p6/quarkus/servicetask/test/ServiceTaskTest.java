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
import org.lorislab.p6.quarkus.servicetask.runtime.ProcessExecutor;
import org.lorislab.p6.quarkus.servicetask.runtime.ServiceTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;

public class ServiceTaskTest {

    Logger log = LoggerFactory.getLogger(ServiceTaskTest.class);

    @Inject
    ProcessExecutor executor;

    @Inject
    ServiceTaskClient serviceTaskClient;

    @RegisterExtension
    static final QuarkusUnitTest config =   new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(SimpleProcess.class)
            );

    @Test
    @DisplayName("Service task test")
    public void testServiceTask() {
        Assertions.assertNotNull(executor);
        Assertions.assertNotNull(serviceTaskClient);

        ServiceTaskInput input = new ServiceTaskInput("process1", "1.0", "service1"
        , Collections.singletonMap("INPUT","I1"));
        ServiceTaskOutput out = executor.execute(input);
        Assertions.assertNotNull(out);
        Assertions.assertNotNull(out.getData());
        Assertions.assertFalse(out.getData().isEmpty());
        Assertions.assertEquals("VALUE1_I1", out.getData().get("KEY1"));
    }
}
