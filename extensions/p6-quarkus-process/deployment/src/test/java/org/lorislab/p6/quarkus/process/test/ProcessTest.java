package org.lorislab.p6.quarkus.process.test;

import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.lorislab.p6.quarkus.process.DeploymentService;
import org.lorislab.p6.quarkus.process.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ProcessTest {

    @Inject
    ProcessService processService;

    @Inject
    DeploymentService deploymentService;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
            );

    @Test
    @DisplayName("Process test")
    public void testProcess() {
        Assertions.assertNotNull(processService);
        Assertions.assertNotNull(deploymentService);
    }
}
