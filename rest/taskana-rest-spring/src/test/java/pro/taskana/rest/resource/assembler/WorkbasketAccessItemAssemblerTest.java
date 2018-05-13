package pro.taskana.rest.resource.assembler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.WorkbasketService;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.rest.TestConfiguration;
import pro.taskana.rest.resource.WorkbasketAccessItemResource;

/**
 * Test for {@link WorkbasketAccessItemAssembler}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class WorkbasketAccessItemAssemblerTest {

    @Autowired
    WorkbasketAccessItemAssembler workbasketAccessItemAssembler;
    @Autowired
    WorkbasketService workbasketService;

    @Test
    public void workBasketAccessItemToResourcePropertiesEqual()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        // given
        WorkbasketAccessItem accessItem = workbasketService.newWorkbasketAccessItem("1", "2");
        accessItem.setPermDistribute(false);
        accessItem.setPermOpen(true);
        accessItem.setPermAppend(false);
        accessItem.setPermRead(false);
        accessItem.setPermTransfer(true);
        accessItem.setPermCustom1(false);
        accessItem.setPermCustom2(false);
        accessItem.setPermCustom3(true);
        accessItem.setPermCustom4(true);
        accessItem.setPermCustom5(true);
        accessItem.setPermCustom6(true);
        accessItem.setPermCustom7(true);
        accessItem.setPermCustom8(true);
        accessItem.setPermCustom9(true);
        accessItem.setPermCustom10(true);
        accessItem.setPermCustom11(true);
        accessItem.setPermCustom12(true);
        // when
        WorkbasketAccessItemResource resource = workbasketAccessItemAssembler.toResource(
            accessItem);
        // then
        testEquality(accessItem, resource);
    }

    @Test
    public void workBasketAccessItemToModelPropertiesEqual() {
        // given
        WorkbasketAccessItemResource resource = new WorkbasketAccessItemResource();
        resource.setAccessId("10");
        resource.setAccessItemId("120");
        resource.setWorkbasketId("1");
        resource.setPermRead(true);
        resource.setPermAppend(false);
        resource.setPermDistribute(false);
        resource.setPermOpen(false);
        resource.setPermTransfer(true);
        resource.setPermCustom1(false);
        resource.setPermCustom2(false);
        resource.setPermCustom3(false);
        resource.setPermCustom4(false);
        resource.setPermCustom5(true);
        resource.setPermCustom6(false);
        resource.setPermCustom7(false);
        resource.setPermCustom8(false);
        resource.setPermCustom9(false);
        resource.setPermCustom10(false);
        resource.setPermCustom11(true);
        resource.setPermCustom12(false);
        // when
        WorkbasketAccessItem accessItem = workbasketAccessItemAssembler.toModel(resource);
        // then
        testEquality(accessItem, resource);
    }

    private void testEquality(WorkbasketAccessItem accessItem,
        WorkbasketAccessItemResource resource) {
        Assert.assertEquals(accessItem.getAccessId(), resource.accessId);
        Assert.assertEquals(accessItem.getId(), resource.accessItemId);
        Assert.assertEquals(accessItem.getWorkbasketId(), resource.workbasketId);
        Assert.assertEquals(accessItem.isPermAppend(), resource.permAppend);
        Assert.assertEquals(accessItem.isPermCustom1(), resource.permCustom1);
        Assert.assertEquals(accessItem.isPermCustom2(), resource.permCustom2);
        Assert.assertEquals(accessItem.isPermCustom3(), resource.permCustom3);
        Assert.assertEquals(accessItem.isPermCustom4(), resource.permCustom4);
        Assert.assertEquals(accessItem.isPermCustom5(), resource.permCustom5);
        Assert.assertEquals(accessItem.isPermCustom6(), resource.permCustom6);
        Assert.assertEquals(accessItem.isPermCustom7(), resource.permCustom7);
        Assert.assertEquals(accessItem.isPermCustom8(), resource.permCustom8);
        Assert.assertEquals(accessItem.isPermCustom9(), resource.permCustom9);
        Assert.assertEquals(accessItem.isPermCustom10(), resource.permCustom10);
        Assert.assertEquals(accessItem.isPermCustom11(), resource.permCustom11);
        Assert.assertEquals(accessItem.isPermCustom12(), resource.permCustom12);
        Assert.assertEquals(accessItem.isPermDistribute(), resource.permDistribute);
        Assert.assertEquals(accessItem.isPermRead(), resource.permRead);
        Assert.assertEquals(accessItem.isPermOpen(), resource.permOpen);
        Assert.assertEquals(accessItem.isPermTransfer(), resource.permTransfer);
    }
}
