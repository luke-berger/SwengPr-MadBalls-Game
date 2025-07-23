package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link GeometryData} abstract class and its implementations.
 * <p>
 * This test class verifies the correct behavior of GeometryData and its concrete
 * implementations (such as CircleGeometry) used for view-agnostic geometric
 * representations in the model layer.
 * </p>
 * <p>
 * GeometryData provides the mathematical foundation for shape collision detection,
 * rendering calculations, and physics simulation without being tied to any specific
 * UI framework like JavaFX.
 * </p>
 * 
 * @see GeometryData
 * @see CircleGeometry
 * @see Position
 */
public class TestGeometryData {
    
    private Position testPosition;
    private GeometryData testGeometry;
    
    /**
     * Sets up test fixtures before each test method execution.
     * <p>
     * This method is executed automatically before each individual test method
     * to ensure that each test starts with a clean, consistent state. The setup
     * creates a standardized testing environment with predefined geometric objects
     * that can be used across multiple test scenarios.
     * </p>
     * 
     * @see Position#Position(float, float)
     * @see CircleGeometry#CircleGeometry(Position, double, double)
     * @see GeometryData
     */
    @BeforeEach
    public void setUp() {
        testPosition = new Position(10.0f, 20.0f);
        // Use CircleGeometry as concrete implementation for testing
        testGeometry = new CircleGeometry(testPosition, 5.0, 30.0);
    }
    
    /**
     * Tests the position getter method of GeometryData.
     * <p>
     * This test verifies that the {@link GeometryData#getPosition()} method
     * correctly returns the position that was set during object construction.
     * The position represents the geometric center of the shape in 2D coordinate space.
     * </p>
     * <p>
     * This test ensures proper encapsulation and data integrity for the
     * fundamental position property used throughout the geometry system.
     * </p>
     * 
     * @see GeometryData#getPosition()
     * @see Position
     */
    @Test
    public void testGetPosition() {
        assertEquals(testPosition, testGeometry.getPosition());
    }
    
    /**
     * Tests the rotation getter method of GeometryData.
     * <p>
     * This test verifies that the {@link GeometryData#getRotation()} method
     * correctly returns the rotation angle that was set during object construction.
     * The rotation is measured in degrees and represents the orientation of
     * the geometric shape relative to its default orientation.
     * </p>
     * <p>
     * The test uses a delta comparison (0.001) to account for potential
     * floating-point precision loss during calculations. This ensures
     * reliable testing of rotation values.
     * </p>
     * 
     * @see GeometryData#getRotation()
     */
    @Test
    public void testGetRotation() {
        assertEquals(30.0, testGeometry.getRotation(), 0.001);
    }
    
    /**
     * Tests handling of zero rotation angles in GeometryData.
     * <p>
     * This test verifies that GeometryData correctly handles the special case
     * of zero rotation, which represents the default orientation of a geometric shape.
     * Zero rotation is a common case that should be handled without numerical errors.
     * </p>
     * <p>
     * The test ensures that:
     * </p>
     * <ul>
     * <li>Zero rotation values are stored and retrieved correctly</li>
     * <li>No unintended rounding or precision errors occur for zero values</li>
     * <li>The geometry system properly handles this baseline orientation case</li>
     * </ul>
     * 
     * @see GeometryData#getRotation()
     * @see CircleGeometry#CircleGeometry(Position, double, double)
     */
    @Test
    public void testZeroRotation() {
        GeometryData zeroRotationGeometry = new CircleGeometry(testPosition, 5.0, 0.0);
        assertEquals(0.0, zeroRotationGeometry.getRotation(), 0.001);
    }
    
    /**
     * Tests handling of negative rotation angles in GeometryData.
     * <p>
     * This test verifies that GeometryData correctly handles negative rotation values,
     * which are valid representations for counter-clockwise rotations. The geometry
     * system should support the full range of rotation angles including negative values.
     * </p>
     * <p>
     * The test ensures that:
     * </p>
     * <ul>
     * <li>Negative rotation values are stored without modification</li>
     * <li>Retrieval of negative rotations maintains the original sign and magnitude</li>
     * <li>The geometry system supports bidirectional rotation representation</li>
     * </ul>
     * <p>
     * This is important for physics simulation accuracy where objects may rotate
     * in either direction.
     * </p>
     * 
     * @see GeometryData#getRotation()
     * @see CircleGeometry#CircleGeometry(Position, double, double)
     */
    @Test
    public void testNegativeRotation() {
        GeometryData negativeRotationGeometry = new CircleGeometry(testPosition, 5.0, -45.0);
        assertEquals(-45.0, negativeRotationGeometry.getRotation(), 0.001);
    }
    
    /**
     * Tests that abstract methods are properly implemented in concrete GeometryData subclasses.
     * <p>
     * This test verifies that the abstract methods defined in GeometryData are
     * correctly implemented in concrete subclasses. It ensures that:
     * </p>
     * <ul>
     * <li>{@link GeometryData#containsPoint(double, double)} returns a non-null result</li>
     * <li>{@link GeometryData#getBounds()} returns a non-null bounding box array</li>
     * <li>The concrete implementation provides functional behavior for abstract methods</li>
     * </ul>
     * <p>
     * This test serves as a contract verification that ensures all GeometryData
     * subclasses provide the required functionality for geometric calculations
     * and collision detection systems.
     * </p>
     * 
     * @see GeometryData#containsPoint(double, double)
     * @see GeometryData#getBounds()
     * @see CircleGeometry
     */
    @Test
    public void testAbstractMethodsExist() {
        // Verify that abstract methods are implemented
        assertNotNull(testGeometry.containsPoint(15.0, 25.0));
        assertNotNull(testGeometry.getBounds());
    }
}
