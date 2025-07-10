package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TestAbstractObject {
    @Test
    public void testAbstractObject() {
        AbstractObject testObj = new AbstractObject(); 
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj.getClass());

        String testString = "test";
        Size testSize = new Size();
        testObj = new AbstractObject(testString, testString, testSize);
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj);
        assertEquals(testString, testObj.getName());
        assertEquals(testString, testObj.getType());
        assertEquals(testSize, testObj.getSize());
        assertEquals(0.0f, testObj.getAngle(), 0.0001f);
        assertEquals(false, testObj.isWinning());

        Physics testPhysics = new Physics();
        testObj = new AbstractObject(testString, testString, testSize, testPhysics);
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj);
        assertEquals(testString, testObj.getName());
        assertEquals(testString, testObj.getType());
        assertEquals(testSize, testObj.getSize());
        assertEquals(0.0f, testObj.getAngle(), 0.0001f);
        assertEquals(false, testObj.isWinning());
        assertEquals(testPhysics, testObj.getPhysics());
    }

    @Test 
    public void testStringSetterGetter() {
        String testString = "this is a test: succes!";
        AbstractObject testObj = new AbstractObject();
        testObj.setSprite(testString);
        testObj.setColour(testString);
        assertEquals(testString, testObj.getSprite());
        assertEquals(testString, testObj.getColour());
    }

    @Test
    public void testSizeSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        Size testSize = new Size();
        testObj.setSize(testSize);
        assertEquals(testSize, testObj.getSize()); 
    }

    @Test
    public void testAngleSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        float testAngle = 123.4f; 
        testObj.setAngle(testAngle);
        assertEquals(testAngle, testObj.getAngle(), 0.0001f);
    }

    @Test
    public void testPhysicsSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        Physics testPhysics = new Physics();
        testObj.setPhysics(testPhysics);
        assertEquals(testPhysics, testObj.getPhysics());
    }

    @Test
    public void testWinningSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        boolean winning = true;
        testObj.setWinning(winning);
        assertEquals(winning, testObj.isWinning());
    }
}
