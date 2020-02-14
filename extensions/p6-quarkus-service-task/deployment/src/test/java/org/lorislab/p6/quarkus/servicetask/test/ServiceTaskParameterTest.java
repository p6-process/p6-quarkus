package org.lorislab.p6.quarkus.servicetask.test;

import io.quarkus.test.QuarkusUnitTest;
import io.vertx.core.json.JsonObject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskInput;
import org.lorislab.p6.quarkus.servicetask.ServiceTaskOutput;
import org.lorislab.p6.quarkus.servicetask.runtime.ProcessExecutor;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

public class ServiceTaskParameterTest {

    @Inject
    ProcessExecutor executor;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(ParameterProcess.class)
            );

    @Test
    @DisplayName("Service task parameter test")
    public void testServiceTaskParameter() {
        ParameterModel model = new ParameterModel();
        model.param1 = "i1";
        model.param2 = "i2";
        Map<String, Object> tmp = Collections.singletonMap("input", model);
        JsonObject msg = new JsonObject(tmp);
        String payload = msg.toString();

        JsonObject json = new JsonObject(payload);

        ServiceTaskInput input = new ServiceTaskInput("parameterProcess", "1.0", "parameter1", json);
        ServiceTaskOutput out = executor.execute(input);
        Assertions.assertNotNull(out);

        Assertions.assertNotNull(out.getData());
        Assertions.assertFalse(out.getData().isEmpty());
        Object object = out.getData().get("KEY1");
        Assertions.assertTrue(object instanceof ParameterModel);

        ParameterModel result = (ParameterModel) object;

        Assertions.assertEquals(model.param1 + "_out1", result.param1);
        Assertions.assertEquals(model.param2 + "_out2", result.param2);
    }
}
