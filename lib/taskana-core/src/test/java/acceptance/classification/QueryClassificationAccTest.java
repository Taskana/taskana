package acceptance.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Acceptance test for all "get classification" scenarios.
 */
public class QueryClassificationAccTest extends AbstractAccTest {

    public QueryClassificationAccTest() {
        super();
    }

    @Test
    public void testFindClassificationsByCategoryAndDomain()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<Classification> classificationList = classificationService.createClassificationQuery()
            .category("MANUAL")
            .domain("DOMAIN_A")
            .list();

        assertNotNull(classificationList);
        assertEquals(2, classificationList.size());
    }

    @Ignore
    @Test
    public void testGetOneClassificationForMultipleDomains()
        throws SQLException, ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<Classification> classifications = classificationService.createClassificationQuery()
            .type("L10000")
            .domain("DOMAIN_A", "DOMAIN_B")
            .list();

        assertNotNull(classifications);
        assertEquals(2, classifications.size());
        assertEquals(2, classifications.get(0).getPriority());
        assertEquals(22, classifications.get(1).getPriority());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}
