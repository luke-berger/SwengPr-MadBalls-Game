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

}
