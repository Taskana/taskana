package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.Attachment;
import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.AttachmentPersistenceException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.impl.AttachmentImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.security.JAASRunner;

/**
 * Acceptance test for the usecase of adding/removing an attachment of a task and update the result correctly.
 */
@RunWith(JAASRunner.class)
public class UpdateTaskAttachmentsAccTest extends AbstractAccTest {

    private Task task;
    private Attachment attachment;
    TaskService taskService;

    public UpdateTaskAttachmentsAccTest() {
        super();
    }

    @Before
    public void setUpMethod()
        throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException, SQLException,
        WorkbasketNotFoundException, InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException,
        AttachmentPersistenceException {
        taskService = taskanaEngine.getTaskService();
        task = taskService.getTask("TKI:000000000000000000000000000000000000");
        attachment = createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_A", "SYSTEM_B", "INSTANCE_B", "ArchiveId",
                "12345678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(3));
        task.getAttachments().clear();
        taskService.updateTask(task);
        assertThat(task, not(equalTo(null)));
    }

    @Test
    public void testAddNewAttachment()
        throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException,
        AttachmentPersistenceException {
        int attachmentCount = task.getAttachments().size();
        task.addAttachment(attachment);

        task = taskService.updateTask(task);

        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount + 1));
        assertThat(task.getAttachments().get(0).getClassificationSummary().getKey(), equalTo("DOCTYPE_DEFAULT"));
    }

    @Test(expected = AttachmentPersistenceException.class)
    public void testAddNewAttachmentTwiceWithoutTaskanaMethodWillThrowAttachmentPersistenceException()
        throws TaskNotFoundException, WorkbasketNotFoundException, ClassificationNotFoundException,
        InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException, NotAuthorizedException,
        AttachmentPersistenceException {
        int attachmentCount = 0;
        task.getAttachments().clear();
        task = taskService.updateTask(task);
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount));

        AttachmentImpl attachment = (AttachmentImpl) this.attachment;
        attachment.setId("TAI:000017");
        task.getAttachments().add(attachment);
        task.getAttachments().add(attachment);
        task.getAttachments().add(attachment);
        task = taskService.updateTask(task);
    }

    @Test
    public void testAddExistingAttachmentAgainWillUpdateWhenNotEqual()
        throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException,
        AttachmentPersistenceException {
        // Add attachment before
        task = taskService.getTask(task.getId());
        int attachmentCount = task.getAttachments().size();
        task.addAttachment(attachment);
        task = taskService.updateTask(task);
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount + 1));

        // Change sth. and add same (id) again - override/update
        String newChannel = "UPDATED EXTERNAL SINCE LAST ADD";
        attachmentCount = task.getAttachments().size();
        Attachment updatedAttachment = task.getAttachments().get(0);
        updatedAttachment.setChannel(newChannel);
        task.addAttachment(updatedAttachment);
        task = taskService.updateTask(task);
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount));
        assertThat(task.getAttachments().get(0).getChannel(), equalTo(newChannel));
    }

    @Test
    public void testAddExistingAttachmentAgainWillDoNothingWhenEqual()
        throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException,
        AttachmentPersistenceException {
        // Add Attachment before
        int attachmentCount = task.getAttachments().size();
        ((AttachmentImpl) attachment).setId("TAI:0001");
        task.addAttachment(attachment);
        task.addAttachment(attachment); // overwrite, same id
        task.addAttachment(attachment); // overwrite, same id
        task = taskService.updateTask(task);
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount + 1));

        // Add same again - ignored
        attachmentCount = task.getAttachments().size();
        Attachment redundantAttachment = task.getAttachments().get(0);
        task.addAttachment(redundantAttachment);
        task = taskService.updateTask(task);
        assertThat(task.getAttachments().size(), equalTo(attachmentCount));
    }

    @Test
    public void testAddAttachmentAsNullValueWillBeIgnored()
        throws TaskNotFoundException, WorkbasketNotFoundException, ClassificationNotFoundException,
        InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException, NotAuthorizedException,
        AttachmentPersistenceException {
        // Try to add a single NULL-Element
        int attachmentCount = task.getAttachments().size();
        task.addAttachment(null);
        task = taskService.updateTask(task);
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount));

        // Try to set the Attachments to NULL and update it
        ((TaskImpl) task).setAttachments(null);
        task = taskService.updateTask(task);
        assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // locally, not persisted
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // persisted values not changed

        // Test no NullPointer on NULL-Value and removing it on current data.
        // New loading can do this, but returned value should got this "function", too.
        attachmentCount = task.getAttachments().size();
        task.getAttachments().add(null);
        task.getAttachments().add(null);
        task.getAttachments().add(null);
        task = taskService.updateTask(task);
        assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // locally, not persisted
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // persisted values not changed
    }

    @Test
    public void testRemoveAttachment()
        throws TaskNotFoundException, WorkbasketNotFoundException, ClassificationNotFoundException,
        InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException, NotAuthorizedException,
        AttachmentPersistenceException {
        task.addAttachment(attachment);
        task = taskService.updateTask(task);

        int attachmentCount = task.getAttachments().size();
        Attachment attachmentToRemove = task.getAttachments().get(0);
        task.removeAttachment(attachmentToRemove.getId());
        task = taskService.updateTask(task);
        assertThat(task.getAttachments().size(), equalTo(attachmentCount - 1)); // locally, removed and not persisted
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount - 1)); // persisted, values removed
    }

    @Test
    public void testRemoveAttachmentWithNullAndNotAddedId()
        throws TaskNotFoundException, WorkbasketNotFoundException, ClassificationNotFoundException,
        InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException, NotAuthorizedException,
        AttachmentPersistenceException {
        task.addAttachment(attachment);
        task = taskService.updateTask(task);
        int attachmentCount = task.getAttachments().size();

        task.removeAttachment(null);
        task = taskService.updateTask(task);
        assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // locally, nothing changed
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // persisted, still same

        task.removeAttachment("INVALID ID HERE");
        task = taskService.updateTask(task);
        assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // locally, nothing changed
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount)); // persisted, still same
    }

    @Test
    public void testUpdateAttachment()
        throws TaskNotFoundException, WorkbasketNotFoundException, ClassificationNotFoundException,
        InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException, NotAuthorizedException,
        AttachmentPersistenceException {
        ((TaskImpl) task).setAttachments(new ArrayList<>());
        task = taskService.updateTask(task);

        Attachment attachment = this.attachment;
        task.addAttachment(attachment);
        task = taskService.updateTask(task);
        int attachmentCount = task.getAttachments().size();

        String newChannel = attachment.getChannel() + "-X";
        task.getAttachments().get(0).setChannel(newChannel);
        task = taskService.updateTask(task);
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(attachmentCount));
        assertThat(task.getAttachments().get(0).getChannel(), equalTo(newChannel));
    }

    @Test
    public void modifyExistingAttachment()
        throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException,
        AttachmentPersistenceException {
        // setup test
        assertThat(task.getAttachments().size(), equalTo(0));
        task.addAttachment(attachment);
        Attachment attachment2 = createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_B", "SYSTEM_C", "INSTANCE_C", "ArchiveId",
                "ABC45678901234567890123456789012345678901234567890"),
            "ROHRPOST", "2018-01-15", createSimpleCustomProperties(4));
        task.addAttachment(attachment2);
        task = taskService.updateTask(task);
        task = taskService.getTask(task.getId());

        assertThat(task.getAttachments().size(), equalTo(2));
        List<Attachment> attachments = task.getAttachments();
        boolean rohrpostFound = false;
        boolean emailFound = false;
        for (Attachment att : attachments) {
            String channel = att.getChannel();
            int custAttSize = att.getCustomAttributes().size();
            if ("ROHRPOST".equals(channel)) {
                rohrpostFound = true;
            } else if ("E-MAIL".equals(channel)) {
                emailFound = true;
            } else {
                fail("unexpected attachment detected " + att);
            }
            assertTrue(("ROHRPOST".equals(channel) && custAttSize == 4)
                || ("E-MAIL".equals(channel) && custAttSize == 3));
        }
        assertTrue(rohrpostFound && emailFound);

        // modify existing attachment
        for (Attachment att : task.getAttachments()) {
            if (att.getCustomAttributes().size() == 3) {
                att.setChannel("FAX");
                break;
            }
        }
        task = taskService.updateTask(task);
        task = taskService.getTask(task.getId());

        rohrpostFound = false;
        boolean faxFound = false;

        for (Attachment att : task.getAttachments()) {
            String channel = att.getChannel();
            int custAttSize = att.getCustomAttributes().size();
            if ("FAX".equals(channel)) {
                faxFound = true;
            } else if ("ROHRPOST".equals(channel)) {
                rohrpostFound = true;
            } else {
                fail("unexpected attachment detected " + att);
            }

            assertTrue(("ROHRPOST".equals(channel) && custAttSize == 4)
                || ("FAX".equals(channel) && custAttSize == 3));
        }
        assertTrue(faxFound && rohrpostFound);
    }

    @Test
    public void replaceExistingAttachments()
        throws TaskNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
        WorkbasketNotFoundException, InvalidArgumentException, ConcurrencyException, InvalidWorkbasketException,
        AttachmentPersistenceException {
        // setup test
        assertThat(task.getAttachments().size(), equalTo(0));
        task.addAttachment(attachment);
        Attachment attachment2 = createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_B", "SYSTEM_C", "INSTANCE_C", "ArchiveId",
                "ABC45678901234567890123456789012345678901234567890"),
            "E-MAIL", "2018-01-15", createSimpleCustomProperties(4));
        task.addAttachment(attachment2);
        task = taskService.updateTask(task);
        task = taskService.getTask(task.getId());
        assertThat(task.getAttachments().size(), equalTo(2));
        assertThat(task.getAttachments().get(0).getClassificationSummary().getKey(), equalTo("DOCTYPE_DEFAULT"));
        assertThat(task.getAttachments().get(1).getCustomAttributes().size(), equalTo(4));

        Attachment attachment3 = createAttachment("DOCTYPE_DEFAULT",
            createObjectReference("COMPANY_C", "SYSTEM_7", "INSTANCE_7", "ArchiveId",
                "ABC4567890123456789012345678901234567890DEF"),
            "DHL", "2018-01-15", createSimpleCustomProperties(4));

        // replace existing attachments by new via addAttachment call
        task.getAttachments().clear();
        task.addAttachment(attachment3);
        task = taskService.updateTask(task);
        assertThat(task.getAttachments().size(), equalTo(1));
        assertThat(task.getAttachments().get(0).getChannel(), equalTo("DHL"));

        // setup environment for 2nd version of replacement (list.add call)
        task.getAttachments().add(attachment2);
        task = taskService.updateTask(task);
        assertThat(task.getAttachments().size(), equalTo(2));
        assertThat(task.getAttachments().get(1).getChannel(), equalTo("E-MAIL"));
        // replace attachments
        task.getAttachments().clear();
        task.getAttachments().add(attachment3);
        task = taskService.updateTask(task);
        assertThat(task.getAttachments().size(), equalTo(1));
        assertThat(task.getAttachments().get(0).getChannel(), equalTo("DHL"));
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
