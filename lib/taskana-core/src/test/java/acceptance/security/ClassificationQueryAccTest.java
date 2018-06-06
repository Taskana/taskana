package acceptance.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for classification queries and authorization.
 *
 * @author bbr
 */
@RunWith(JAASRunner.class)
public class ClassificationQueryAccTest extends AbstractAccTest {

    public ClassificationQueryAccTest() {
        super();
    }

    @Test
    public void testFindClassificationsByDomainUnauthenticated() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .list();

        assertNotNull(classificationSummaryList);
        assertEquals(17, classificationSummaryList.size());
    }

    @WithAccessId(userName = "businessadmin")
    @Test
    public void testFindClassificationsByDomainBusinessAdmin() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .list();

        assertNotNull(classificationSummaryList);
        assertEquals(17, classificationSummaryList.size());
    }

    @WithAccessId(userName = "admin")
    @Test
    public void testFindClassificationsByDomainAdmin() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .domainIn("DOMAIN_A")
            .list();

        assertNotNull(classificationSummaryList);
        assertEquals(17, classificationSummaryList.size());
    }

}
