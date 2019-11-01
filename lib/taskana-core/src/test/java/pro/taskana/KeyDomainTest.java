package pro.taskana;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Unit test of KeyDomain.
 */
public class KeyDomainTest {

    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String DOMAIN1 = "domain1";
    private static final String DOMAIN2 = "domain2";

    @Test
    public void getKey() {
        KeyDomain keyDomain = new KeyDomain(KEY1, DOMAIN1);
        assertEquals(KEY1, keyDomain.getKey());
    }

    @Test
    public void setKey() {
        KeyDomain keyDomain = new KeyDomain(KEY1, DOMAIN1);
        keyDomain.setKey(KEY2);
        assertEquals(KEY2, keyDomain.getKey());
    }

    @Test
    public void getDomain() {
        KeyDomain keyDomain = new KeyDomain(KEY1, DOMAIN1);
        assertEquals(DOMAIN1, keyDomain.getDomain());
    }

    @Test
    public void setDomain() {
        KeyDomain keyDomain = new KeyDomain(KEY1, DOMAIN1);
        keyDomain.setDomain(DOMAIN2);
        assertEquals(DOMAIN2, keyDomain.getDomain());
    }

    @Test
    public void toString1() {
        assertNotNull(new KeyDomain(KEY1, DOMAIN1).toString());
    }

    @Test
    public void hashCode1() {
        assertNotEquals(new KeyDomain(KEY1, DOMAIN1).hashCode(), new KeyDomain(KEY2, DOMAIN1).hashCode());
        assertNotEquals(new KeyDomain(KEY1, DOMAIN1).hashCode(), new KeyDomain(KEY1, DOMAIN2).hashCode());
        assertEquals(new KeyDomain(KEY1, DOMAIN1).hashCode(), new KeyDomain(KEY1, DOMAIN1).hashCode());
    }

    @Test
    public void equals1() {
        KeyDomain keyDomain = new KeyDomain(KEY1, DOMAIN1);
        KeyDomain keyDomainEq = new KeyDomain(KEY1, DOMAIN1);
        KeyDomain keyDomainNeq = new KeyDomain(KEY2, DOMAIN1);

        assertEquals(keyDomain, keyDomain);
        assertEquals(keyDomain, keyDomainEq);
        assertNotEquals(keyDomain, null);
        assertNotEquals(keyDomain, new Object());
        assertNotEquals(keyDomain, keyDomainNeq);
    }

    @Test
    public void equals2() {
        KeyDomain keyDomain = new KeyDomain(KEY1, DOMAIN1);
        KeyDomain keyDomainNullKey = new KeyDomain(null, DOMAIN1);
        KeyDomain keyDomainNullKey2 = new KeyDomain(null, DOMAIN1);
        KeyDomain keyDomainNullDomain = new KeyDomain(KEY1, null);
        KeyDomain keyDomainNullDomain2 = new KeyDomain(KEY1, null);
        KeyDomain keyDomainNull = new KeyDomain(null, null);
        KeyDomain keyDomainNull2 = new KeyDomain(null, null);

        assertEquals(keyDomainNullKey, keyDomainNullKey2);
        assertEquals(keyDomainNullDomain, keyDomainNullDomain2);
        assertEquals(keyDomainNull, keyDomainNull2);

        assertNotEquals(keyDomain, keyDomainNullKey);
        assertNotEquals(keyDomain, keyDomainNullDomain);
        assertNotEquals(keyDomain, keyDomainNull);

    }
}
