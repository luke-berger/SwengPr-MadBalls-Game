package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TestGameObject extends TestAbstractObject {
    @Test
    public void testGameObject() {
        GameObject testObj = new GameObject();
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj.getClass());

        Position testPosition = new Position();
        testObj.setPosition(testPosition);
        assertEquals(testPosition, testObj.getPosition());

        String testString = "test";
        Size testSize = new Size();
        testObj = new GameObject(testString, testString, testPosition, testSize);
        assertNotEquals(null, testObj);
        assertNotEquals(null, testObj.getClass());
    } 
}
