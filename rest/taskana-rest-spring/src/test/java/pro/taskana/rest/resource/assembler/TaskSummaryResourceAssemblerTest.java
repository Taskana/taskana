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
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.WorkbasketSummaryImpl;
import pro.taskana.rest.TestConfiguration;
import pro.taskana.rest.resource.TaskSummaryResource;
/**
 * Test for {@link TaskSummaryResourceAssembler}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class TaskSummaryResourceAssemblerTest {
    @Autowired
    TaskSummaryResourceAssembler taskSummaryResourceAssembler;

    @Autowired
    ClassificationSummaryResourceAssembler classificationSummaryResourceAssembler;

    @Autowired
    WorkbasketSummaryResourceAssembler workbasketSummaryResourceAssembler;

    @Autowired
    TaskService taskService;

    @Autowired
    ClassificationService classificationService;

    @Autowired
    WorkbasketService workbasketService;

    @Test
    public void TaskSummaryToResource() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // given
        TaskImpl task = (TaskImpl) taskService.newTask("Some Id");
        task.setId("Some Id");
        String[] stringMethods = new String[]{"Name", "Creator", "Note", "BusinessProcessId", "ParentBusinessProcessId", "Owner"};
        String[] instantArray = new String[]{"Modified", "Claimed", "Created", "Completed", "Due", "Planned"};

        for (String i : stringMethods) {
            String methodName = "set" + i;
            Method method = task.getClass().getMethod(methodName, String.class);
            method.invoke(task, "String for " + i);
        }

        for (int i = 1; i <= 16; i++) {
            String methodName = "setCustom" + i;
            Method method = task.getClass().getMethod(methodName, String.class);
            method.invoke(task, "Custom" + i);
        }

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
        task.setPriority(1);

        TaskSummary taskSummary = task.asSummary();

        // when
        TaskSummaryResource taskSummaryResource = taskSummaryResourceAssembler.toResource(taskSummary);

        // then
        String taskSummaryMethodName;
        String taskSummaryResourceMethodName;
        Method taskSummaryMethod;
        Method taskResourceMethod;
        for (String i : stringMethods) {
            taskSummaryMethodName = "get" + i;
            taskSummaryMethod = taskSummary.getClass().getMethod(taskSummaryMethodName);
            taskResourceMethod = taskSummaryResource.getClass().getMethod(taskSummaryMethodName);
            Assert.assertEquals(taskSummaryMethod.invoke(taskSummary), taskResourceMethod.invoke(taskSummaryResource));
        }

        for (String i : instantArray) {
            taskSummaryMethodName = "get" + i;
            taskSummaryMethod = taskSummary.getClass().getMethod(taskSummaryMethodName);
            taskResourceMethod = taskSummaryResource.getClass().getMethod(taskSummaryMethodName);
            Assert.assertEquals(taskSummaryMethod.invoke(taskSummary).toString(), taskResourceMethod.invoke(taskSummaryResource));
        }

//        for (int i = 1; i <= 16; i++){
//            taskSummaryMethodName = "getCustomAttribute";
//            taskSummaryResourceMethodName = "getCustom" + i;
//            taskSummaryMethod = taskSummary.getClass().getMethod(taskSummaryMethodName, String.class);
//            taskResourceMethod = taskSummaryResource.getClass().getMethod(taskSummaryResourceMethodName);
//            Assert.assertEquals(taskSummaryMethod.invoke(taskSummary, "" + i), taskResourceMethod.invoke(taskSummaryResource));
//        }

    }
}
