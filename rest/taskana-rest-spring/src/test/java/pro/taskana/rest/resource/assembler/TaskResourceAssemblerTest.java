package pro.taskana.rest.resource.assembler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import pro.taskana.*;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.WorkbasketSummaryImpl;
import pro.taskana.rest.TestConfiguration;
import pro.taskana.rest.resource.ClassificationSummaryResource;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

/**
 * Test for {@link TaskResourceAssembler}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class TaskResourceAssemblerTest {
    @Autowired
    TaskResourceAssembler assembler;

    @Autowired
    TaskService taskService;

    @Autowired
    ClassificationService classificationService;

    @Autowired
    WorkbasketService workbasketService;

    @Autowired
    ClassificationSummaryResourceAssembler classificationSummaryResourceAssembler;

    @Autowired
    WorkbasketSummaryResourceAssembler workbasketSummaryResourceAssembler;

    private String[] instantArray = new String[]{"Modified", "Claimed", "Created", "Completed", "Due", "Planned"};
    private String[] stringMethods = new String[]{"Name", "Creator", "Description", "Note", "BusinessProcessId", "ParentBusinessProcessId", "Owner"};

    @Test
    public void TaskToResource() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        TaskImpl task = (TaskImpl) taskService.newTask("1");
        task.setId("Some Id");
        task = fillValues(task);

        for (String i : instantArray) {
            String methodname = "set" + i;
            Method method = task.getClass().getMethod(methodname, Instant.class);
            method.invoke(task, Instant.now());
        }

        ClassificationImpl classification = (ClassificationImpl) classificationService.newClassification("123", "ABC", "DEF");
        classification.setId("C2P0");
        classification.setCategory("Some Category");
        classification.setName("Some Name");
        classification.setParentId("Some ParentId");
        classification.setPriority(1);
        ClassificationSummary classificationSummary = classification.asSummary();
        task.setClassificationSummary(classificationSummary);

        WorkbasketSummaryImpl workbasketSummary = (WorkbasketSummaryImpl) workbasketService.newWorkbasket("1",
                "DOMAIN_A").asSummary();
        workbasketSummary.setId("Some Id");
        task.setWorkbasketSummary(workbasketSummary);

        task.setState(TaskState.CLAIMED);
        // MISSING: task.setPrimaryObjRef();

        // when
        TaskResource taskResource = assembler.toResource(task);

        // then
        testEquality(task, taskResource);
    }

    @Test
    public void ResourceToModel() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InvalidArgumentException {
        // given
        TaskResource resource = new TaskResource();
        resource.setTaskId("Some Id");
        resource = fillValues(resource);

        for (String i : instantArray) {
            String methodname = "set" + i;
            Method method = resource.getClass().getMethod(methodname, String.class);
            method.invoke(resource, Instant.now().toString());
        }

        ClassificationSummaryResource classificationSummaryResource = new ClassificationSummaryResource();
        classificationSummaryResource.setClassificationId("C2P0");
        classificationSummaryResource.setCategory("Some Category");
        classificationSummaryResource.setDomain("Some Domain");
        classificationSummaryResource.setKey("Some Key");
        classificationSummaryResource.setName("Some Name");
        classificationSummaryResource.setParentId("Some ParentId");
        classificationSummaryResource.setPriority(1);
        classificationSummaryResource.setType("Type A");
        resource.setClassificationSummaryResource(classificationSummaryResource);

        WorkbasketSummaryResource workbasketSummaryResource = new WorkbasketSummaryResource();
        workbasketSummaryResource.setWorkbasketId("Some Id");
        workbasketSummaryResource.setDescription("Some Description");
        workbasketSummaryResource.setOrgLevel1("Some Org Leves");
        resource.setWorkbasketSummaryResource(workbasketSummaryResource);

        resource.setState(TaskState.CLAIMED);

        // when
        TaskImpl task = (TaskImpl) assembler.toModel(resource);

        // then
        testEquality(task, resource);
    }

    public <T> T fillValues(T resource) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        for (String i : stringMethods) {
            String methodName = "set" + i;
            Method method = resource.getClass().getMethod(methodName, String.class);
            method.invoke(resource, "String for " + i);
        }

        for (int i = 1; i <= 16; i++) {
            String methodName = "setCustom" + i;
            Method method = resource.getClass().getMethod(methodName, String.class);
            method.invoke(resource, "Custom" + i);
        }
        return resource;
    }

    private void testEquality(TaskImpl task, TaskResource taskResource) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName;
        Method taskMethod;
        Method taskResourceMethod;
        for (String i : stringMethods) {
            methodName = "get" + i;
            taskMethod = task.getClass().getMethod(methodName);
            taskResourceMethod = taskResource.getClass().getMethod(methodName);
            Assert.assertEquals(taskMethod.invoke(task), taskResourceMethod.invoke(taskResource));
        }

        for (String i : instantArray) {
            methodName = "get" + i;
            taskMethod = task.getClass().getMethod(methodName);
            taskResourceMethod = taskResource.getClass().getMethod(methodName);
            Assert.assertEquals(taskMethod.invoke(task).toString(), taskResourceMethod.invoke(taskResource));
        }

        for (int i = 1; i <= 16; i++) {
            methodName = "getCustom" + i;
            taskMethod = task.getClass().getMethod(methodName);
            taskResourceMethod = taskResource.getClass().getMethod(methodName);
            Assert.assertEquals(taskMethod.invoke(task), taskResourceMethod.invoke(taskResource));
        }

        Assert.assertEquals(task.getClassificationSummary(), classificationSummaryResourceAssembler.toModel(taskResource.getClassificationSummaryResource()));
        Assert.assertEquals(task.getWorkbasketSummary(), workbasketSummaryResourceAssembler.toModel(taskResource.getWorkbasketSummaryResource()));
        Assert.assertEquals(task.getState(), taskResource.getState());
        Assert.assertEquals(task.getId(), taskResource.getTaskId());
    }
}
