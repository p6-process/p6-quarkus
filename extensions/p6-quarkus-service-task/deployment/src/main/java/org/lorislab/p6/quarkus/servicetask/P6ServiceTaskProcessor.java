package org.lorislab.p6.quarkus.servicetask;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.*;
import org.lorislab.p6.quarkus.servicetask.runtime.ServiceTaskId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

class P6ServiceTaskProcessor {

    private static final Logger log = LoggerFactory.getLogger(P6ServiceTaskProcessor.class);

    static final String FEATURE_NAME = "p6-service-task";

    static final String ATTR_ID = "id";
    static final String ATTR_VERSION = "version";
    static final String ATTR_NAME = "name";
    static final String ATTR_PROCESS = "process";
    static final DotName PROCESS_ANO = DotName.createSimple(Process.class.getName());
    static final DotName SERVICE_TASK_ANO = DotName.createSimple(ServiceTask.class.getName());
    static final DotName SERVICE_TASK_ID = DotName.createSimple(ServiceTaskId.class.getName());
    static final Type SERVICE_TASK_INPUT = Type.create(DotName.createSimple(ServiceTaskInput.class.getName()), Type.Kind.CLASS);
    static final Type SERVICE_TASK_OUTPUT = Type.create(DotName.createSimple(ServiceTaskOutput.class.getName()), Type.Kind.CLASS);

    @BuildStep
    CapabilityBuildItem capability() {
        return new CapabilityBuildItem(FEATURE_NAME);
    }

    @BuildStep
    FeatureBuildItem createFeatureItem() {
        return new FeatureBuildItem(FEATURE_NAME);
    }

    /**
     * Beans annotated with @Process or @ServiceTaskId should never be removed
     * @return the list of un-removable beans
     */
    @BuildStep
    public List<UnremovableBeanBuildItem> unRemovableBeans() {
        return Arrays.asList(
                new UnremovableBeanBuildItem(new UnremovableBeanBuildItem.BeanClassAnnotationExclusion(PROCESS_ANO)),
                new UnremovableBeanBuildItem(new UnremovableBeanBuildItem.BeanClassAnnotationExclusion(SERVICE_TASK_ID)));
    }

    @BuildStep
    void buildProcessClient(CombinedIndexBuildItem indexBuildItem,
                            BuildProducer<GeneratedBeanBuildItem> generatedClass) {

        Collection<AnnotationInstance> annotations = indexBuildItem.getIndex().getAnnotations(PROCESS_ANO);

        Set<String> ids = new HashSet<>();
        List<ServiceTaskMethodItem> result = new ArrayList<>();
        if (annotations != null && !annotations.isEmpty()) {
            for (AnnotationInstance annotation : annotations) {
                collectServiceTaskMethods(indexBuildItem.getIndex(), annotation.target().asClass(), result, ids);
            }
        }

        if (!result.isEmpty()) {
            for (ServiceTaskMethodItem method : result) {
                new ServiceTaskBeanGenerator(method, generatedClass).createServiceTaskBean();
            }
        }
    }

    private void collectServiceTaskMethods(IndexView index, ClassInfo clazz, List<ServiceTaskMethodItem> items, Set<String> ids) {
        for (MethodInfo method : clazz.methods()) {
            ServiceTaskMethodItem item = serviceTaskMethod(index, clazz, method);
            if (item != null) {
                String id = item.getId() + item.getVersion() + item.getName();
                if (ids.contains(id)) {
                    throw new IllegalStateException(String.format(
                            "The service task id for process '%s' version '%s' service task '%s' already exists. [method: %s, bean: %s]",
                            item.getId(), item.getVersion(), item.getName(),
                            method, clazz));
                }
                ids.add(id);
                items.add(item);
            }
        }

        DotName superClassName = clazz.superName();
        if (superClassName != null) {
            ClassInfo superClass = index.getClassByName(superClassName);
            if (superClass != null) {
                collectServiceTaskMethods(index, superClass, items, ids);
            }
        }
    }

    private ServiceTaskMethodItem serviceTaskMethod(IndexView index, ClassInfo clazz, MethodInfo method) {
        AnnotationInstance serviceTaskAno = method.annotation(SERVICE_TASK_ANO);
        if (serviceTaskAno != null) {

            validateMethod(method);

            Map<String, AnnotationValue> serviceTaskValues = getValues(index, serviceTaskAno);
            String name = getValueString(ATTR_NAME, serviceTaskValues);

            Map<String, AnnotationValue> serviceTaskProcessValues = getValues(index, serviceTaskValues.get(ATTR_PROCESS).asNested());
            String id = getValueString(ATTR_ID, serviceTaskProcessValues);
            String version = getValueString(ATTR_VERSION, serviceTaskProcessValues);

            if (id.isBlank() || version.isBlank()) {
                AnnotationInstance ca = clazz.classAnnotation(PROCESS_ANO);
                Map<String, AnnotationValue> processValues = getValues(index, ca);

                if (id.isBlank()) {
                    id = getValueString(ATTR_ID, processValues);
                }
                if (version.isBlank()) {
                    version = getValueString(ATTR_VERSION, processValues);
                }
            }

            log.debug("Found service task method method {} declared on {} for the service task {} {} {}", method, clazz, id, version, name);
            return new ServiceTaskMethodItem(clazz, method, id, version, name);
        }
        return null;
    }

    private void validateMethod(MethodInfo method) {
        short mod = method.flags();
        if (!Modifier.isPublic(mod) || Modifier.isStatic(mod)) {

            throw new IllegalStateException(String.format(
                    "Service task method must be public and none static [method: %s, bean: %s]", method, method.declaringClass()));
        }

        // Validate method params and return type
        List<Type> params = method.parameters();
        if (params.size() > 1
                || (params.size() == 1 && !params.get(0).equals(SERVICE_TASK_INPUT))) {
            throw new IllegalStateException(String.format(
                    "Invalid service task method parameters %s [method: %s, bean: %s]", params,
                    method, method.declaringClass()));
        }
        if (!method.returnType().equals(SERVICE_TASK_OUTPUT)) {
            throw new IllegalStateException(
                    String.format("Service task method must return ServiceTaskOutput [method: %s, bean: %s]",
                            method, method.declaringClass()));
        }
    }


    private Map<String, AnnotationValue> getValues(IndexView index, AnnotationInstance anno) {
        List<AnnotationValue> values = anno.valuesWithDefaults(index);
        if (values.isEmpty()) {
            return Collections.emptyMap();
        }
        return values
                .stream()
                .collect(Collectors.toMap(AnnotationValue::name, p -> p));
    }

    private String getValueString(String name, Map<String, AnnotationValue> values) {
        AnnotationValue av = values.get(name);
        if (av != null) {
            return av.asString();
        }
        return "";
    }
}
