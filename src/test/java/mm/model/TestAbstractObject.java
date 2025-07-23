package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link AbstractObject} class.
 * <p>
 * This test class verifies the correct behavior of AbstractObject constructors
 * and basic functionality. AbstractObject serves as the base class for game objects
 * in the simulation, providing common fields and methods for name, size, and physics properties.
 * </p>
 * <p>
 * The tests cover all constructor variants to ensure proper initialization
 * of object properties under different scenarios.
 * </p>
 * 
 * @see AbstractObject
 * @see Size
 * @see Physics
 */
public class TestAbstractObject {
    /**
     * Tests all constructors of the AbstractObject class.
     * <p>
     * This test method verifies the proper functionality of all three AbstractObject constructors:
     * </p>
     * <ul>
     * <li>Default constructor - creates an AbstractObject with default values</li>
     * <li>Basic constructor with name, type, and size parameters</li>
     * <li>Extended constructor with name, type, size, and physics parameters</li>
     * </ul>
     * <p>
     * Each constructor is tested through helper methods that verify:
     * - Successful object instantiation
     * - Correct class type
     * - Proper initialization of provided parameters
     * - Default values for unspecified parameters
     * </p>
     * 
     * @see AbstractObject#AbstractObject()
     * @see AbstractObject#AbstractObject(String, String, Size)
     * @see AbstractObject#AbstractObject(String, String, Size, Physics)
     */
    @Test
    public void testAbstractObject() {
        boolean test1 = testConstructer1();
        
        String testString = "test";
        Size testSize = new Size();
        boolean test2 = testConstructor2(testString, testSize);

        Physics testPhysics = new Physics();
        boolean test3 = testConstructor3(testString, testSize, testPhysics);
        assertTrue(test1 && test2 && test3);
    }

    /**
     * Tests the string property setter and getter methods of AbstractObject.
     * <p>
     * This test verifies the correct functionality of string-based properties:
     * </p>
     * <ul>
     * <li>{@link AbstractObject#setSprite(String)} - sets the sprite identifier</li>
     * <li>{@link AbstractObject#getSprite()} - retrieves the sprite identifier</li>
     * <li>{@link AbstractObject#setColour(String)} - sets the color value</li>
     * <li>{@link AbstractObject#getColour()} - retrieves the color value</li>
     * </ul>
     * <p>
     * The test uses a string containing special characters to ensure
     * proper handling of various string values.
     * </p>
     * 
     * @see AbstractObject#setSprite(String)
     * @see AbstractObject#getSprite()
     * @see AbstractObject#setColour(String)
     * @see AbstractObject#getColour()
     */
    @Test 
    public void testStringSetterGetter() {
        String testString = "this is a test: succes!";
        AbstractObject testObj = new AbstractObject();
        testObj.setSprite(testString);
        testObj.setColour(testString);
        assertEquals(testString, testObj.getSprite());
        assertEquals(testString, testObj.getColour());
    }

    /**
     * Tests the Size property setter and getter methods of AbstractObject.
     * <p>
     * This test verifies that the AbstractObject can correctly:
     * </p>
     * <ul>
     * <li>Accept a {@link Size} object through {@link AbstractObject#setSize(Size)}</li>
     * <li>Return the same Size object through {@link AbstractObject#getSize()}</li>
     * <li>Maintain object reference integrity (the returned object should be the same instance)</li>
     * </ul>
     * <p>
     * Size objects are fundamental to game objects as they define the physical
     * dimensions used for rendering and collision detection.
     * </p>
     * 
     * @see AbstractObject#setSize(Size)
     * @see AbstractObject#getSize()
     * @see Size
     */
    @Test
    public void testSizeSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        Size testSize = new Size();
        testObj.setSize(testSize);
        assertEquals(testSize, testObj.getSize()); 
    }

    /**
     * Tests the angle property setter and getter methods of AbstractObject.
     * <p>
     * This test verifies that the AbstractObject correctly handles rotation angles:
     * </p>
     * <ul>
     * <li>{@link AbstractObject#setAngle(float)} - sets the rotation angle in degrees</li>
     * <li>{@link AbstractObject#getAngle()} - retrieves the current rotation angle</li>
     * <li>Floating-point precision is maintained during storage and retrieval</li>
     * </ul>
     * <p>
     * The angle property is crucial for object orientation in the physics simulation
     * and visual rendering system. The test uses a delta comparison to account for
     * potential floating-point precision loss.
     * </p>
     * 
     * @see AbstractObject#setAngle(float)
     * @see AbstractObject#getAngle()
     */
    @Test
    public void testAngleSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        float testAngle = 123.4f; 
        testObj.setAngle(testAngle);
        assertEquals(testAngle, testObj.getAngle(), 0.0001f);
    }

    /**
     * Tests the Physics property setter and getter methods of AbstractObject.
     * <p>
     * This test verifies that the AbstractObject can correctly:
     * </p>
     * <ul>
     * <li>Accept a {@link Physics} object through {@link AbstractObject#setPhysics(Physics)}</li>
     * <li>Return the same Physics object through {@link AbstractObject#getPhysics()}</li>
     * <li>Maintain object reference integrity for physics properties</li>
     * </ul>
     * <p>
     * Physics objects define how game objects behave in the physics simulation,
     * including properties like density, friction, and restitution that are
     * essential for realistic object interactions.
     * </p>
     * 
     * @see AbstractObject#setPhysics(Physics)
     * @see AbstractObject#getPhysics()
     * @see Physics
     */
    @Test
    public void testPhysicsSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        Physics testPhysics = new Physics();
        testObj.setPhysics(testPhysics);
        assertEquals(testPhysics, testObj.getPhysics());
    }

    /**
     * Tests the winning flag setter and getter methods of AbstractObject.
     * <p>
     * This test verifies the correct functionality of the winning state management:
     * </p>
     * <ul>
     * <li>{@link AbstractObject#setWinning(boolean)} - sets the winning trigger state</li>
     * <li>{@link AbstractObject#isWinning()} - retrieves the current winning state</li>
     * <li>Boolean state is properly maintained and retrieved</li>
     * </ul>
     * <p>
     * The winning flag is used to identify objects that trigger win conditions
     * when certain game objectives are met (e.g., ball reaching a target area).
     * This is crucial for level completion logic in puzzle mode.
     * </p>
     * 
     * @see AbstractObject#setWinning(boolean)
     * @see AbstractObject#isWinning()
     */
    @Test
    public void testWinningSetterGetter() {
        AbstractObject testObj = new AbstractObject();
        boolean winning = true;
        testObj.setWinning(winning);
        assertEquals(winning, testObj.isWinning());
    }

    private boolean testConstructer1() {
        AbstractObject testObj = new AbstractObject();
        assertNotNull(testObj);
        assertEquals(AbstractObject.class, testObj.getClass());
        return true;
    }
    private boolean testConstructor2(String testString, Size testSize) {
        AbstractObject testObj = new AbstractObject(testString, testString, testSize);
        testAssertions(testObj, testString, testSize, null);
        return true;
    }

    private boolean testConstructor3(String testString, Size testSize, Physics testPhysics) {
        AbstractObject testObj = new AbstractObject(testString, testString, testSize, testPhysics);
        testAssertions(testObj, testString, testSize, testPhysics);
        return true;
    }

    private void testAssertions(AbstractObject testObj, String testString, Size testSize, Physics testPhysics) {
        assertNotNull(testObj);
        assertEquals(AbstractObject.class, testObj.getClass());
        assertEquals(testString, testObj.getName());
        assertEquals(testString, testObj.getType());
        assertEquals(testSize, testObj.getSize());
        testAssertions2(testObj, testPhysics);
    }

    private void testAssertions2(AbstractObject testObj, Physics testPhysics) {
        assertEquals(0.0f, testObj.getAngle(), 0.0001f);
        //test condition inverted for less static imports because of PMD violations
        assertTrue(!testObj.isWinning());
        assertEquals(testPhysics, testObj.getPhysics());
    }
}
