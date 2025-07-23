package mm.model;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for collision detection tests providing common utilities and constants.
 * <p>
 * This abstract base class centralizes the common setup, helper methods, and string
 * constants used across all collision detection test suites. It eliminates code
 * duplication and ensures consistent test infrastructure across different test
 * categories.
 * </p>
 * <p>
 * The base class provides:
 * </p>
 * <ul>
 * <li>Mock simulation model setup</li>
 * <li>JBox2D world initialization</li>
 * <li>Common test object creation utilities</li>
 * <li>String constants to avoid duplicate literals</li>
 * </ul>
 * 
 * @see GeometricCollisionDetection
 * @see PhysicsGeometryPair
 * @see SimulationModel
 */
public class CollisionDetectionTestBase {

    // Test object identifier constants
    protected static final String OBJECT_1 = "object1";
    protected static final String OBJECT_2 = "object2";
    protected static final String RECT = "rect";
    protected static final String CIRCLE = "circle";
    protected static final String RECT_1 = "rect1";
    protected static final String RECT_2 = "rect2";
    protected static final String CIRCLE_1 = "circle1";
    protected static final String CIRCLE_2 = "circle2";
    
    // Special object type constants
    protected static final String WIN_ZONE = "winZone";
    protected static final String WIN_PLAT = "winPlat";
    protected static final String NO_PLACE = "noPlace";
    protected static final String UNSUPPORTED = "unsupported";
    protected static final String ZERO_RECT = "zeroRect";
    protected static final String ZERO_CIRCLE = "zeroCircle";
    protected static final String ROTATED_RECT = "rotatedRect";
    protected static final String CIRCLE_PAIR = "circlePair";
    protected static final String RECT_PAIR = "rectPair";

    /** Mock simulation model for testing collision detection behavior. */
    protected SimulationModel mockModel;
    /** The collision detection system under test. */
    protected GeometricCollisionDetection collisionDetection;
    /** JBox2D world for creating test physics bodies. */
    protected World testWorld;
    /** List of geometry pairs used in collision tests. */
    protected List<PhysicsGeometryPair> testPairs;
    
    /**
     * Sets up test fixtures before each test method.
     * <p>
     * Initializes mocked dependencies and creates a fresh collision detection
     * instance for each test. Also sets up a JBox2D world and empty test pairs
     * list to ensure test isolation and consistency.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        mockModel = mock(SimulationModel.class);
        collisionDetection = new GeometricCollisionDetection(mockModel);
        testWorld = new World(new Vec2(0, 0));
        testPairs = new ArrayList<>();
        
        when(mockModel.getGeometryPairs()).thenReturn(testPairs);
    }
    
    /**
     * Helper method to create a test physics body with specified user data.
     * <p>
     * Creates a minimal JBox2D body with the given user data string.
     * This is used throughout the tests to create bodies with identifiable
     * names for collision detection testing.
     * </p>
     * 
     * @param userData the string identifier to associate with the body
     * @return a new JBox2D Body with the specified user data
     */
    protected Body createTestBody(String userData) {
        BodyDef bodyDef = new BodyDef();
        Body body = testWorld.createBody(bodyDef);
        body.setUserData(userData);
        return body;
    }
    
    /**
     * Helper method to create a PhysicsGeometryPair with specified geometry and user data.
     * <p>
     * Combines geometry data with a physics body to create a complete pair
     * suitable for collision detection testing. The body is automatically
     * created with the specified user data identifier.
     * </p>
     * 
     * @param geometry the geometry data for the pair (can be null for testing)
     * @param userData the string identifier for the associated physics body
     * @return a new PhysicsGeometryPair ready for testing
     */
    protected PhysicsGeometryPair createTestPair(GeometryData geometry, String userData) {
        Body body = createTestBody(userData);
        return new PhysicsGeometryPair(geometry, body);
    }
    
    /**
     * Helper method to create a standard rectangle geometry for testing.
     * <p>
     * Creates a rectangular geometry with commonly used dimensions for testing
     * purposes, reducing test setup boilerplate code.
     * </p>
     * 
     * @param x the x position of the rectangle
     * @param y the y position of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @return a new RectangleGeometry with the specified parameters
     */
    protected RectangleGeometry createRectangle(double x, double y, double width, double height) {
        return new RectangleGeometry(new Position((float) x, (float) y), width, height);
    }
    
    /**
     * Helper method to create a rectangle geometry with rotation for testing.
     * <p>
     * Creates a rectangular geometry with rotation applied, commonly used in
     * rotation-aware collision detection tests.
     * </p>
     * 
     * @param pos the position of the rectangle center
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param rotation the rotation angle in degrees
     * @return a new RectangleGeometry with the specified parameters and rotation
     */
    protected RectangleGeometry createRotatedRectangle(Position pos, double width, double height, double rotation) {
        return new RectangleGeometry(pos, width, height, rotation);
    }
    
    /**
     * Helper method to create a standard circle geometry for testing.
     * <p>
     * Creates a circular geometry with commonly used parameters for testing
     * purposes, reducing test setup boilerplate code.
     * </p>
     * 
     * @param x the x position of the circle center
     * @param y the y position of the circle center
     * @param radius the radius of the circle
     * @return a new CircleGeometry with the specified parameters
     */
    protected CircleGeometry createCircle(double x, double y, double radius) {
        return new CircleGeometry(new Position((float) x, (float) y), radius);
    }
}
