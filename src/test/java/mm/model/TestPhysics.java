package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test-Klasse für die Physics-Klasse.
 * Testet die Funktionalität der Physics-Klasse inklusive aller Setter/Getter-Methoden
 * für physikalische Eigenschaften.
 * 
 * @version 1.0
 */
public class TestPhysics {
    
    /**
     * Testet die grundlegende Funktionalität der Physics-Klasse.
     * Überprüft die Objekterstellung und stellt sicher, dass das Objekt nicht null ist.
     */
    @Test
    public void testPhysics() {
        Physics testPhysics = new Physics();
        assertNotEquals(null, testPhysics);
        assertNotEquals(null, testPhysics.getClass());
    }

    /**
     * Testet alle Setter- und Getter-Methoden der Physics-Klasse.
     * Überprüft Density, Friction, Restitution und Shape-Eigenschaften.
     */
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