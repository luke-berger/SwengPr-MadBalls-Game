package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TestPosition {
    @Test
    public void testPosition() {
        Position testPosition = new Position();
        assertNotEquals(null, testPosition);
        assertNotEquals(null, testPosition.getClass());
        float testFloat1 = 1.2f;
        float testFloat2 = 3.4f;
        testPosition = new Position(testFloat1, testFloat2);
        assertNotEquals(null, testPosition);
        assertNotEquals(null, testPosition.getClass());
        assertEquals(testFloat1, testPosition.getX());
        assertEquals(testFloat2, testPosition.getY());
    }

    @Test
    public void testPositionSetterGetter() {
        Position testPosition = new Position();
        float testFloat = 0.123f;
        testPosition.setX(testFloat);
        testPosition.setY(testFloat);
        assertEquals(testFloat, testPosition.getX(), 0.00001f);
        assertEquals(testFloat, testPosition.getY(), 0.00001f);
    }
}
