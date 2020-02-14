package org.lorislab.p6.quarkus.servicetask;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.util.HashUtil;
import io.quarkus.gizmo.*;
import org.jboss.jandex.*;
import org.lorislab.p6.quarkus.servicetask.runtime.ServiceTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static io.quarkus.gizmo.DescriptorUtils.extToInt;

public class ServiceTaskBeanGenerator {

    private static final Logger log = LoggerFactory.getLogger(ServiceTaskBeanGenerator.class);

    static final String INVOKER_SUFFIX = "_ServiceTask";

    private static final int ACCESS_PACKAGE_PROTECTED = 0;

    private final BuildProducer<GeneratedBeanBuildItem> generatedBean;

    private final ServiceTaskMethodItem serviceTask;

    public ServiceTaskBeanGenerator(ServiceTaskMethodItem serviceTask, BuildProducer<GeneratedBeanBuildItem> generatedBean) {
        this.generatedBean = generatedBean;
        this.serviceTask = serviceTask;
    }

    public void createServiceTaskBean() {
        MethodInfo methodInfo = serviceTask.getMethod();

        MethodInfo method = serviceTask.getMethod();
        ClassInfo ci = serviceTask.getClazz();

        if (log.isDebugEnabled()) {
            log.debug("Create service task bean for the class {} and method {}", method.declaringClass(), method.name());
        }

        StringBuilder sigBuilder = new StringBuilder();
        sigBuilder.append(method.name()).append("_").append(method.returnType().name().toString());
        for (Type i : method.parameters()) {
            sigBuilder.append(i.name().toString());
        }

        String baseName;
        if (ci.enclosingClass() != null) {
            baseName = DotNames.simpleName(ci.enclosingClass()) + "_" + DotNames.simpleName(ci.name());
        } else {
            baseName = DotNames.simpleName(ci.name());
        }

        String targetPackage = DotNames.packageName(ci.name());
        String generatedName = targetPackage.replace('.', '/') + "/" + baseName + INVOKER_SUFFIX + "_" + methodInfo.name() + "_"
                + HashUtil.sha1(sigBuilder.toString());

        // @ApplicationScoped @ServiceTaskId("process_version_serviceTask") public class BeanExecutor
        ClassCreator executor = ClassCreator.builder()
                .classOutput(this::writeGeneratedBeanBuildItem)
                .className(generatedName)
                .interfaces(ServiceTaskExecutor.class)
                .build();

        executor.addAnnotation(ApplicationScoped.class);
        executor.addAnnotation(createServiceTaskId());

        // inject bean field
        FieldCreator beanField = executor.getFieldCreator("bean", extToInt(ci.name().toString()));
        beanField.setModifiers(ACCESS_PACKAGE_PROTECTED);
        beanField.addAnnotation(Inject.class);

        // create execute method
        MethodCreator executeMethod = executor.getMethodCreator("execute", ServiceTaskOutput.class, ServiceTaskInput.class);
        executeMethod.returnValue(
                executeMethod.invokeVirtualMethod(
                        MethodDescriptor.ofMethod(ci.name().toString(), method.name(), ServiceTaskOutput.class,
                                ServiceTaskInput.class),
                        resultHandleFor(beanField, executeMethod), executeMethod.getMethodParam(0))
        );

        executor.close();
        log.debug("Service task bean: {} ", generatedName);
    }

    private static ResultHandle resultHandleFor(FieldCreator field, BytecodeCreator method) {
        FieldDescriptor fieldDescriptor = field.getFieldDescriptor();
        return method.readInstanceField(fieldDescriptor, method.getThis());
    }

    private void writeGeneratedBeanBuildItem(String name, byte[] data) {
        generatedBean.produce(new GeneratedBeanBuildItem(name, data));
    }

    private AnnotationInstance createServiceTaskId() {
        return AnnotationInstance.create(P6ServiceTaskProcessor.SERVICE_TASK_ID, null,
                new AnnotationValue[]{
                        AnnotationValue.createStringValue(P6ServiceTaskProcessor.ATTR_ID, serviceTask.getId()),
                        AnnotationValue.createStringValue(P6ServiceTaskProcessor.ATTR_VERSION, serviceTask.getVersion()),
                        AnnotationValue.createStringValue(P6ServiceTaskProcessor.ATTR_NAME, serviceTask.getName())
                });
    }

}
