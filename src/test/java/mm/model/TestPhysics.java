package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TestPhysics {
    @Test
    public void testPhysics() {
        Physics testPhysics = new Physics();
        assertNotEquals(null, testPhysics);
        assertNotEquals(null, testPhysics.getClass());
    }

    @Test
    public void testSetterGetter() {
        float testDensity = 0.123f;
        float testFriction = 456.7f;
        float testRestitution = 8.90f;
        String testShape = "DYNAMIC";
        Physics testPhysics = new Physics();
        testPhysics.setDensity(testDensity);
        testPhysics.setFriction(testFriction);
        testPhysics.setRestitution(testRestitution);
        testPhysics.setShape(testShape);
        assertEquals(testDensity, testPhysics.getDensity());
        assertEquals(testFriction, testPhysics.getFriction());
        assertEquals(testRestitution, testPhysics.getRestitution());
        assertEquals(testShape, testPhysics.getShape());
    }
}
