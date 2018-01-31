package pro.taskana.impl;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.Attachment;

/**
 * Unit Test for methods needed fot attachment at TaskImpl.<br>
 * This test should test every interaction with Attachments, which means adding, removing, nulling them.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskAttachmentTest {

    @InjectMocks
    private TaskImpl cut;

    @Test
    public void testAddAttachmentWithValidValue() {
        Attachment attachment1 = createAttachment("ID1", "taskId1");
        Attachment attachment2 = createAttachment("ID2", "taskId1");
        Attachment attachment3 = createAttachment("ID3", "taskId1");

        cut.addAttachment(attachment1);
        cut.addAttachment(attachment2);
        cut.addAttachment(attachment3);

        assertThat(cut.getAttachments().size(), equalTo(3));
    }

    @Test
    public void testAddNullValue() {
        Attachment attachment1 = createAttachment("ID1", "taskId1");
        Attachment attachment2 = null;

        cut.addAttachment(attachment1);
        cut.addAttachment(attachment2);

        assertThat(cut.getAttachments().size(), equalTo(1));
    }

    @Test
    public void testAddSameTwice() {
        // Same values, not same REF. Important.
        Attachment attachment1 = createAttachment("ID1", "taskId1");
        Attachment attachment2 = createAttachment("ID1", "taskId1");

        cut.addAttachment(attachment1);
        cut.addAttachment(attachment2);

        assertThat(cut.getAttachments().size(), equalTo(1));

        // Check with not same vlaues (same ID)
        String newChannel = "I will overwrite the other!";
        attachment1.setChannel(newChannel);
        cut.addAttachment(attachment1);
        assertThat(cut.getAttachments().size(), equalTo(1));
        assertThat(cut.getAttachments().get(0).getChannel(), equalTo(newChannel));
    }

    @Test
    public void testRemoveAttachment() {
        // Testing normal way
        Attachment attachment1 = createAttachment("ID1", "taskId1");
        Attachment attachment2 = createAttachment("ID2", "taskId1");
        cut.addAttachment(attachment1);
        cut.addAttachment(attachment2);

        Attachment actual = cut.removeAttachment(attachment2.getId());

        assertThat(cut.getAttachments().size(), equalTo(1));
        assertThat(actual, equalTo(attachment2));
    }

    @Test
    public void testRemoveLoopStopsAtResult() {
        Attachment attachment1 = createAttachment("ID2", "taskId1");
        // adding same uncommon way to test that the loop will stop.
        cut.getAttachments().add(attachment1);
        cut.getAttachments().add(attachment1);
        cut.getAttachments().add(attachment1);
        assertThat(cut.getAttachments().size(), equalTo(3));

        Attachment actual = cut.removeAttachment(attachment1.getId());

        assertThat(cut.getAttachments().size(), equalTo(2));
        assertThat(actual, equalTo(attachment1));
    }

    private Attachment createAttachment(String id, String taskId) {
        AttachmentImpl attachment = new AttachmentImpl();
        attachment.setId(id);
        attachment.setTaskId(taskId);
        return attachment;
    }
}
