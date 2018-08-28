package pro.taskana.rest.resource.assembler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
<<<<<<< HEAD
import pro.taskana.WorkbasketAccessItem;
=======
>>>>>>> a89fa20c... TSK-694 - Show list of all access items valid for a user (REST)
import pro.taskana.WorkbasketAccessItemExtended;
import pro.taskana.WorkbasketService;
import pro.taskana.impl.WorkbasketAccessItemExtendedImpl;
import pro.taskana.rest.TestConfiguration;
import pro.taskana.rest.resource.WorkbasketAccesItemExtendedResource;

/**
 * Test for {@link WorkbasketAccessItemExtendedAssembler}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class WorkbasketAccessItemExtendedAssemblerTest {

    @Autowired
    WorkbasketAccessItemExtendedAssembler workbasketAccessItemExtendedAssembler;

    @Autowired
    WorkbasketService workbasketService;

    @Test
    public void workbasketAccessItemExtendedToResource() {
        // given
        WorkbasketAccessItemExtended workbasketAccessItemExtended = workbasketService.newWorkbasketAccessItemExtended("workbasketId",
                "accessId");

        ((WorkbasketAccessItemExtendedImpl) workbasketAccessItemExtended).setWorkbasketKey("workbasketKey");

        ((WorkbasketAccessItemExtendedImpl) workbasketAccessItemExtended).setId("id");
        workbasketAccessItemExtended.setAccessName("Name");
        workbasketAccessItemExtended.setPermAppend(true);
        workbasketAccessItemExtended.setPermCustom1(true);
        workbasketAccessItemExtended.setPermCustom2(true);
        workbasketAccessItemExtended.setPermCustom3(true);
        workbasketAccessItemExtended.setPermCustom4(true);
        workbasketAccessItemExtended.setPermCustom5(true);
        workbasketAccessItemExtended.setPermCustom6(true);
        workbasketAccessItemExtended.setPermCustom7(true);
        workbasketAccessItemExtended.setPermCustom8(true);
        workbasketAccessItemExtended.setPermCustom9(true);
        workbasketAccessItemExtended.setPermCustom10(true);
        workbasketAccessItemExtended.setPermCustom11(true);
        workbasketAccessItemExtended.setPermCustom12(true);
        workbasketAccessItemExtended.setPermDistribute(true);
        workbasketAccessItemExtended.setPermOpen(true);
        workbasketAccessItemExtended.setPermRead(true);
        workbasketAccessItemExtended.setPermTransfer(true);
        workbasketAccessItemExtended.setPermDistribute(true);
        // when
        WorkbasketAccesItemExtendedResource workbasketAccesItemExtendedResource = workbasketAccessItemExtendedAssembler.toResource(workbasketAccessItemExtended);
        // then
        Assert.assertEquals(workbasketAccessItemExtended.getWorkbasketId(), workbasketAccesItemExtendedResource.workbasketId);
        Assert.assertEquals(workbasketAccessItemExtended.getWorkbasketKey(), workbasketAccesItemExtendedResource.workbasketKey);
        Assert.assertEquals(workbasketAccessItemExtended.getId(), workbasketAccesItemExtendedResource.accessItemId);
        Assert.assertEquals(workbasketAccessItemExtended.getAccessId(), workbasketAccesItemExtendedResource.accessId);
        Assert.assertEquals(workbasketAccessItemExtended.getAccessName(), workbasketAccesItemExtendedResource.accessName);
        Assert.assertEquals(workbasketAccessItemExtended.isPermAppend(), workbasketAccesItemExtendedResource.permAppend);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom1(), workbasketAccesItemExtendedResource.permCustom1);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom2(), workbasketAccesItemExtendedResource.permCustom2);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom3(), workbasketAccesItemExtendedResource.permCustom3);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom4(), workbasketAccesItemExtendedResource.permCustom4);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom5(), workbasketAccesItemExtendedResource.permCustom5);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom6(), workbasketAccesItemExtendedResource.permCustom6);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom7(), workbasketAccesItemExtendedResource.permCustom7);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom8(), workbasketAccesItemExtendedResource.permCustom8);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom9(), workbasketAccesItemExtendedResource.permCustom9);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom10(), workbasketAccesItemExtendedResource.permCustom10);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom11(), workbasketAccesItemExtendedResource.permCustom11);
        Assert.assertEquals(workbasketAccessItemExtended.isPermCustom12(), workbasketAccesItemExtendedResource.permCustom12);
    }
}
