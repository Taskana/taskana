package acceptance.objectreference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.TaskQuery;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.ObjectReference;

/**
 * Acceptance test for all "get classification" scenarios.
 */
public class QueryObjectReferenceAccTest extends AbstractAccTest {

    public QueryObjectReferenceAccTest() {
        super();
    }

    @Test
    public void testFindObjectReferenceByCompany()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

        List<ObjectReference> objectReferenceList = taskQuery.createObjectReferenceQuery()
            .companyIn("Company1", "Company2")
            .list();

        assertNotNull(objectReferenceList);
        assertEquals(2, objectReferenceList.size());
    }

    @Test
    public void testFindObjectReferenceBySystem()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

        List<ObjectReference> objectReferenceList = taskQuery.createObjectReferenceQuery()
            .companyIn("Company1", "Company2")
            .systemIn("System2")
            .list();

        assertNotNull(objectReferenceList);
        assertEquals(1, objectReferenceList.size());
    }

    @Test
    public void testFindObjectReferenceBySystemInstance()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

        List<ObjectReference> objectReferenceList = taskQuery.createObjectReferenceQuery()
            .companyIn("Company1", "Company2")
            .systemInstanceIn("Instance1")
            .list();

        assertNotNull(objectReferenceList);
        assertEquals(1, objectReferenceList.size());
    }

    @Test
    public void testFindObjectReferenceByType()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

        List<ObjectReference> objectReferenceList = taskQuery.createObjectReferenceQuery()
            .typeIn("Type2", "Type3")
            .list();

        assertNotNull(objectReferenceList);
        assertEquals(2, objectReferenceList.size());
    }

    @Test
    public void testFindObjectReferenceByValue()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

        List<ObjectReference> objectReferenceList = taskQuery.createObjectReferenceQuery()
            .valueIn("Value1", "Value3")
            .list();

        assertNotNull(objectReferenceList);
        assertEquals(2, objectReferenceList.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
