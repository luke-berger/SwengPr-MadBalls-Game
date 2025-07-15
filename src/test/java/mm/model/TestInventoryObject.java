package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TestInventoryObject extends TestAbstractObject {
    @Test
    public void testInventoryObject() {
        InventoryObject testObj = new InventoryObject();
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj.getClass());

        int testCount = 12345;
        testObj.setCount(testCount);
        assertEquals(testCount, testObj.getCount());

        String testString = "test";
        Size testSize = new Size();
        testObj = new InventoryObject(testString, testString, testSize);
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj.getClass());
        assertEquals(testString, testObj.getName());
        assertEquals(testString, testObj.getType());
        assertEquals(testSize, testObj.getSize());
        assertEquals(1, testObj.getCount());

        testObj = new InventoryObject(testString, testString, testSize, testCount);
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj.getClass());
        assertEquals(testString, testObj.getName());
        assertEquals(testString, testObj.getType());
        assertEquals(testSize, testObj.getSize());
        assertEquals(testCount, testObj.getCount());
    }
}
