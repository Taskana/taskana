package pro.taskana;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Unittest for {@link ObjectReference}.
 */
public class ObjectReferenceTest {

    ObjectReference objectReference;

    @Before
    public void setUp() {
        objectReference = new ObjectReference();
    }

    @Test
    public void getId() {
        assertNull(objectReference.getId());
    }

    @Test
    public void setId() {
        objectReference.setId("1");
        assertEquals("1", objectReference.getId());
    }

    @Test
    public void getCompany() {
        assertNull(objectReference.getCompany());
    }

    @Test
    public void setCompany() {
        objectReference.setCompany("1");
        assertEquals("1", objectReference.getCompany());
    }

    @Test
    public void getSystem() {
        assertNull(objectReference.getSystem());
    }

    @Test
    public void setSystem() {
        objectReference.setSystem("1");
        assertEquals("1", objectReference.getSystem());
    }

    @Test
    public void getSystemInstance() {
        assertNull(objectReference.getSystemInstance());
    }

    @Test
    public void setSystemInstance() {
        objectReference.setSystemInstance("1");
        assertEquals("1", objectReference.getSystemInstance());
    }

    @Test
    public void getType() {
        assertNull(objectReference.getType());
    }

    @Test
    public void setType() {
        objectReference.setType("1");
        assertEquals("1", objectReference.getType());
    }

    @Test
    public void getValue() {
        assertNull(objectReference.getValue());
    }

    @Test
    public void setValue() {
        objectReference.setValue("1");
        assertEquals("1", objectReference.getValue());
    }

    @Test
    public void toString1() {
        assertNotNull(objectReference.toString());
    }

    @Test
    public void hashCode1() {
        assertEquals(new ObjectReference().hashCode(), new ObjectReference().hashCode());
    }

    @Test
    public void equals1() {

        ObjectReference objectReference = new ObjectReference();
        ObjectReference objectReference1 = new ObjectReference();

        assertEquals(objectReference, objectReference1);
        assertEquals(objectReference, objectReference);
        assertNotEquals(objectReference, null);
        assertNotEquals(objectReference, new Object());

        objectReference1.setId("2");
        assertNotEquals(objectReference, objectReference1);

    }
}
