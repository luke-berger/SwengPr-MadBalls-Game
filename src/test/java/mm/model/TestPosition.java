package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Position} class.
 * <p>
 * This test class verifies the correct behavior of Position objects, which represent
 * 2D coordinates in the simulation space. Position is a fundamental data structure
 * used throughout the model layer for object placement and geometric calculations.
 * </p>
 * <p>
 * The tests cover both constructors (default and parameterized) and verify
 * that coordinate values are properly stored and retrieved.
 * </p>
 * 
 * @see Position
 */
public class TestPosition {
    /**
     * Tests the Position class constructors and basic functionality.
     * <p>
     * This test verifies:
     * </p>
     * <ul>
     * <li>The default constructor creates a valid Position instance</li>
     * <li>The parameterized constructor correctly initializes x and y coordinates</li>
     * <li>The getter methods return the expected coordinate values</li>
     * <li>Both constructors produce instances of the correct class type</li>
     * </ul>
     * <p>
     * The test uses specific floating-point values to ensure precision
     * is maintained during coordinate storage and retrieval.
     * </p>
     * 
     * @see Position#Position()
     * @see Position#Position(float, float)
     * @see Position#getX()
     * @see Position#getY()
     */
    @Test
    public void testPosition() {
        Position testPosition = new Position();
        assertNotNull(testPosition);
        assertEquals(Position.class, testPosition.getClass());
        float testFloat1 = 1.2f;
        float testFloat2 = 3.4f;
        testPosition = new Position(testFloat1, testFloat2);
        assertNotNull(testPosition);
        assertEquals(testFloat1, testPosition.getX());
        assertEquals(testFloat2, testPosition.getY());
    }

    /**
     * Tests the setter and getter methods of the Position class.
     * <p>
     * This test verifies that the Position class correctly handles:
     * </p>
     * <ul>
     * <li>Setting X coordinate values using {@link Position#setX(float)}</li>
     * <li>Setting Y coordinate values using {@link Position#setY(float)}</li>
     * <li>Retrieving X coordinate values using {@link Position#getX()}</li>
     * <li>Retrieving Y coordinate values using {@link Position#getY()}</li>
     * <li>Floating-point precision during value storage and retrieval</li>
     * </ul>
     * <p>
     * The test uses a delta comparison for floating-point equality to account
     * for potential precision loss during arithmetic operations.
     * </p>
     * 
     * @see Position#setX(float)
     * @see Position#setY(float)
     * @see Position#getX()
     * @see Position#getY()
     */
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
