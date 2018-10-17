package acceptance.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.SystemException;

/**
 * Acceptance test for all "get classification" scenarios.
 */
public class GetClassificationAccTest extends AbstractAccTest {

    public GetClassificationAccTest() {
        super();
    }

    @Test
    public void testFindAllClassifications() {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        List<ClassificationSummary> classificationSummaryList = classificationService.createClassificationQuery()
            .list();
        assertNotNull(classificationSummaryList);
    }

    @Test
    public void testGetOneClassificationByKeyAndDomain() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("T6310", "DOMAIN_A");
        assertNotNull(classification);
        assertEquals("CLI:100000000000000000000000000000000011", classification.getId());
        assertEquals("", classification.getParentId());
        assertEquals("AUTOMATIC", classification.getCategory());
        assertEquals("TASK", classification.getType());
        assertEquals(true, classification.getIsValidInDomain());
        assertEquals("T-GUK Honorarrechnung erstellen", classification.getName());
        assertEquals(2, classification.getPriority());
        assertEquals("P2D", classification.getServiceLevel());
        assertEquals("point0815", classification.getApplicationEntryPoint());
        assertEquals("VNR", classification.getCustom1());
        assertEquals("custom2", classification.getCustom2());
        assertEquals("custom3", classification.getCustom3());
        assertEquals("custom4", classification.getCustom4());
        assertEquals("custom5", classification.getCustom5());
        assertEquals("custom6", classification.getCustom6());
        assertEquals("custom7", classification.getCustom7());
        assertEquals("custom8", classification.getCustom8());
    }

    @Test
    public void testGetOneClassificationById() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService
            .getClassification("CLI:100000000000000000000000000000000011");
        assertNotNull(classification);
        assertEquals("T6310", classification.getKey());
        assertEquals("", classification.getParentId());
        assertEquals("AUTOMATIC", classification.getCategory());
        assertEquals("TASK", classification.getType());
        assertEquals("DOMAIN_A", classification.getDomain());
        assertEquals(true, classification.getIsValidInDomain());
        assertEquals("T-GUK Honorarrechnung erstellen", classification.getName());
        assertEquals(2, classification.getPriority());
        assertEquals("P2D", classification.getServiceLevel());
        assertEquals("point0815", classification.getApplicationEntryPoint());
        assertEquals("VNR", classification.getCustom1());
        assertEquals("custom2", classification.getCustom2());
        assertEquals("custom3", classification.getCustom3());
        assertEquals("custom4", classification.getCustom4());
        assertEquals("custom5", classification.getCustom5());
        assertEquals("custom6", classification.getCustom6());
        assertEquals("custom7", classification.getCustom7());
        assertEquals("custom8", classification.getCustom8());
    }

    @Test
    public void testGetClassificationAsSummary() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        ClassificationSummary classification = classificationService
            .getClassification("CLI:100000000000000000000000000000000011").asSummary();
        assertNotNull(classification);
        assertEquals("T6310", classification.getKey());
        assertEquals("", classification.getParentId());
        assertEquals("AUTOMATIC", classification.getCategory());
        assertEquals("TASK", classification.getType());
        assertEquals("DOMAIN_A", classification.getDomain());
        assertEquals("T-GUK Honorarrechnung erstellen", classification.getName());
        //assertEquals("Generali Unterstützungskasse Honorar wird fällig", classification.getDescription());
        assertEquals(2, classification.getPriority());
        assertEquals("P2D", classification.getServiceLevel());
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testGetOneClassificationByIdFails() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService
            .getClassification("CLI:100000000470000000000000000000000011");
        fail("ClassificationNotFoundException was expected");
    }

    @Test
    public void testGetOneClassificationForDomainAndGetClassificationFromMasterDomain()
        throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("L10000", "DOMAIN_B");
        assertNotNull(classification);
        assertEquals("", classification.getDomain());
        assertEquals(999L, classification.getPriority());
    }

    @Test
    public void testGetOneClassificationForMasterDomain() throws ClassificationNotFoundException {
        ClassificationService classificationService = taskanaEngine.getClassificationService();
        Classification classification = classificationService.getClassification("L10000", "");
        assertNotNull(classification);
        assertEquals("", classification.getDomain());
        assertEquals(999L, classification.getPriority());
    }

    @Test(expected = SystemException.class)
    public void testDoesNotExistPropertyClassificationTypeOrItIsEmpty() throws IOException {
        String propertiesFileName = createNewConfigFile("/dummyTestConfig.properties", false, false);
        String delimiter = ";";
        try {
            taskanaEngine.getConfiguration().initTaskanaProperties(propertiesFileName, delimiter);
        } finally {
            deleteFile(propertiesFileName);
        }
    }

    @Test(expected = SystemException.class)
    public void testDoesNotExistPropertyClassificatioCategoryOrItIsEmpty() throws IOException {
        String propertiesFileName = createNewConfigFile("/dummyTestConfig.properties", true, false);
        String delimiter = ";";
        try {
            taskanaEngine.getConfiguration().initTaskanaProperties(propertiesFileName, delimiter);
        } finally {
            deleteFile(propertiesFileName);
        }
    }

    @Test
    public void testWithCategoriesAndClassificationFilled() throws IOException {
        taskanaEngineConfiguration.setClassificationTypes(new ArrayList<String>());
        taskanaEngineConfiguration.setClassificationCategoriesByType(new HashMap<String, List<String>>());
        String propertiesFileName = createNewConfigFile("/dummyTestConfig.properties", true, true);
        String delimiter = ";";
        try {
            taskanaEngine.getConfiguration().initTaskanaProperties(propertiesFileName, delimiter);
        } finally {
            deleteFile(propertiesFileName);
        }
        assertFalse(taskanaEngineConfiguration.getClassificationTypes().isEmpty());
        assertFalse(taskanaEngineConfiguration.getClassificationCategoriesByType(taskanaEngineConfiguration.getClassificationTypes().get(0)).isEmpty());
        assertEquals(taskanaEngineConfiguration.getClassificationTypes().size(), 2);
        assertEquals(taskanaEngineConfiguration.getClassificationCategoriesByType(taskanaEngineConfiguration.getClassificationTypes().get(0)).size(), 4);
        assertEquals(taskanaEngineConfiguration.getClassificationCategoriesByType(taskanaEngineConfiguration.getClassificationTypes().get(1)).size(), 1);
    }

    private String createNewConfigFile(String filename, boolean addingTypes, boolean addingClassification) throws IOException {
        String userHomeDirectroy = System.getProperty("user.home");
        String propertiesFileName = userHomeDirectroy + filename;
        File f = new File(propertiesFileName);
        if (!f.exists()) {
            try (PrintWriter writer = new PrintWriter(propertiesFileName, "UTF-8")) {
                writer.println("taskana.roles.Admin =Holger|Stefan");
                writer.println("taskana.roles.businessadmin  = ebe  | konstantin ");
                writer.println("taskana.roles.user = nobody");
                if (addingTypes) {
                    writer.println("taskana.classification.types= TASK , document");
                }
                if (addingClassification) {
                    writer.println("taskana.classification.categories.task= EXTERNAL, manual, autoMAtic, Process");
                    writer.println("taskana.classification.categories.document= EXTERNAL");
                }
            } catch (IOException e) {
                throw e;
            }
        }
        return propertiesFileName;
    }

    private void deleteFile(String propertiesFileName) {
        System.out.println("about to delete " + propertiesFileName);
        File f = new File(propertiesFileName);
        if (f.exists() && !f.isDirectory()) {
            FileUtils.delete(propertiesFileName);
        }
    }

}
