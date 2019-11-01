package pro.taskana;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of BulkOperationResults.
 */
public class BulkOperationResultsTest {

    private static final Integer ID1 = 1;
    private static final Integer ID2 = 2;
    private static final RuntimeException ERROR1 = new RuntimeException("error1");
    private static final RuntimeException ERROR2 = new RuntimeException("error2");

    BulkOperationResults<Integer, RuntimeException> bulkOperationResults;

    @Before
    public void setUp() {
        bulkOperationResults = new BulkOperationResults<Integer, RuntimeException>();
    }

    @Test
    public void getErrorMap() {
        Map<Integer, RuntimeException> map = bulkOperationResults.getErrorMap();
        assertNotNull(map);
    }

    @Test
    public void addError() {
        bulkOperationResults.addError(ID1, ERROR1);
        assertEquals(ERROR1, bulkOperationResults.getErrorForId(ID1));
    }

    @Test
    public void containsErrors() {
        assertFalse(bulkOperationResults.containsErrors());
        bulkOperationResults.addError(ID1, ERROR1);
        assertTrue(bulkOperationResults.containsErrors());
    }

    @Test
    public void getErrorForId() {

        assertNull(bulkOperationResults.getErrorForId(ID1));
        bulkOperationResults.addError(ID1, ERROR1);
        assertEquals(ERROR1, bulkOperationResults.getErrorForId(ID1));
    }

    @Test
    public void getFailedIds() {
        bulkOperationResults.addError(ID1, ERROR1);
        bulkOperationResults.addError(ID2, ERROR2);
        assertEquals(2, bulkOperationResults.getFailedIds().size());
    }

    @Test
    public void clearErrors() {
        bulkOperationResults.addError(ID1, ERROR1);
        bulkOperationResults.addError(ID2, ERROR2);
        bulkOperationResults.clearErrors();
        assertEquals(0, bulkOperationResults.getFailedIds().size());
    }

    @Test
    public void addAllErrors() {
        bulkOperationResults.addError(ID1, ERROR1);
        bulkOperationResults.addError(ID2, ERROR2);
        assertEquals(2, bulkOperationResults.getFailedIds().size());

        BulkOperationResults<Integer, RuntimeException> bulkOperationResults2 = new BulkOperationResults<Integer, RuntimeException>();
        bulkOperationResults2.addAllErrors(bulkOperationResults);
        assertEquals(2, bulkOperationResults2.getFailedIds().size());

    }

    @Test
    public void mapBulkOperationResults() {

        bulkOperationResults.addError(ID1, ERROR1);
        BulkOperationResults<Integer, Exception> bulkOperationResults2 = bulkOperationResults.mapBulkOperationResults();
        assertTrue(bulkOperationResults2.containsErrors());

    }

    @Test
    public void toString1() {
        assertNotNull(bulkOperationResults.toString());
    }
}
