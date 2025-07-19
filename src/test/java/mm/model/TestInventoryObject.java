package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TestInventoryObject extends TestAbstractObject {
    @Test
    public void testInventoryObject() {
        InventoryObject testObj = testConstructer1();

        int testCount = 12345;
        testObj.setCount(testCount);
        assertEquals(testCount, testObj.getCount());

        String testString = "test";
        Size testSize = new Size();

        testConstructor2(testString, testSize);        
        testConstructor3(testString, testSize, testCount);
    }

    private InventoryObject testConstructer1() {
        InventoryObject testObj = new InventoryObject();
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj.getClass());
        return testObj;
    }
    private void testConstructor2(String testString, Size testSize) {
        InventoryObject testObj = new InventoryObject(testString, testString, testSize);
        int testCount = 0; 
        testAssertions(testObj, testString, testSize, testCount);
    }

    private void testConstructor3(String testString, Size testSize, int testCount) {
        InventoryObject testObj = new InventoryObject(testString, testString, testSize, testCount);
        testAssertions(testObj, testString, testSize, testCount);
    }

    private void testAssertions(InventoryObject testObj, String testString, Size testSize, int count) {
        int testCount = (count != 0) ? count : 1;
        assertNotNull(testObj);
        assertEquals(testString, testObj.getName());
        assertEquals(testString, testObj.getType());
        assertEquals(testSize, testObj.getSize());
        assertEquals(testCount, testObj.getCount());
    }
}
