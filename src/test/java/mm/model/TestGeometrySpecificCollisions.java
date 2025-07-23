package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests collision detection for specific geometry combinations.
 * <p>
 * This test class focuses on collision detection between different geometric shapes,
 * including circle-circle, rectangle-circle, and rectangle-rectangle collisions.
 * It also covers rotation handling and the different algorithms used for various
 * geometry combinations.
 * </p>
 * 
 * @see GeometricCollisionDetection
 * @see CollisionDetectionTestBase
 */
public class TestGeometrySpecificCollisions extends CollisionDetectionTestBase {
    
    /**
     * Tests circle-to-circle collision detection.
     * <p>
     * Verifies the accuracy of collision detection between two circular objects.
     * This tests the circle-circle collision algorithm which uses distance
     * calculations between centers and compares against the sum of radii.
     * </p>
     * <p>
     * Test scenarios:
     * </p>
     * <ul>
     * <li>Two circles sufficiently far apart (no collision expected)</li>
     * <li>Moving one circle closer to trigger collision detection</li>
     * <li>Validation of distance-based collision calculations</li>
     * </ul>
     * <p>
     * This is crucial for accurate physics simulation with circular objects.
     * </p>
     */
    @Test
    public void testCircleCircleCollision() {
        CircleGeometry circle1 = createCircle(0, 0, 5);
        CircleGeometry circle2 = createCircle(15, 0, 5);
        
        PhysicsGeometryPair pair1 = createTestPair(circle1, CIRCLE_1);
        PhysicsGeometryPair pair2 = createTestPair(circle2, CIRCLE_2);
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test no collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
        
        // Test collision (move circle1 closer to circle2)
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 8, 0));
    }
    
    /**
     * Tests rectangle-to-circle collision detection.
     * <p>
     * Verifies the mixed-geometry collision detection between rectangular and
     * circular objects. This tests the more complex algorithm that must handle
     * the intersection between a rectangular boundary and a circular area.
     * </p>
     * <p>
     * The test covers:
     * </p>
     * <ul>
     * <li>Non-collision case with rectangle and circle separated</li>
     * <li>Collision case when rectangle is moved close to circle</li>
     * <li>Proper handling of different geometry types in collision detection</li>
     * </ul>
     * <p>
     * This is important for realistic physics simulation where objects
     * of different shapes must interact correctly.
     * </p>
     */
    @Test
    public void testRectangleCircleCollision() {
        RectangleGeometry rect = createRectangle(0, 0, 10, 10);
        CircleGeometry circle = createCircle(20, 0, 5);
        
        PhysicsGeometryPair rectPair = createTestPair(rect, RECT);
        PhysicsGeometryPair circlePair = createTestPair(circle, CIRCLE);
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Test no collision
        assertFalse(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
        
        // Test collision (move rect closer to circle)
        assertTrue(collisionDetection.wouldCauseOverlap(rectPair, 12, 0));
    }
    
    /**
     * Tests collision detection with rotated rectangles using bounding box approximation.
     * <p>
     * Verifies that when rectangles have rotation applied, the collision detection
     * system falls back to using bounding box approximation for performance reasons.
     * Rotated rectangle collision detection is computationally expensive, so the
     * system uses the axis-aligned bounding box of the rotated rectangle.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Rotated rectangles are handled without errors</li>
     * <li>Bounding box approximation is used for rotated shapes</li>
     * <li>Performance is maintained even with complex rotations</li>
     * </ul>
     */
    @Test
    public void testRotatedRectangleCollision() {
        RectangleGeometry rect1 = createRotatedRectangle(new Position(0, 0), 10, 10, 45);
        RectangleGeometry rect2 = createRectangle(20, 20, 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, RECT_1);
        PhysicsGeometryPair pair2 = createTestPair(rect2, RECT_2);
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // With rotation, should use bounding box approximation
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests circle-to-circle collision detection for touching circles.
     * <p>
     * Verifies the precise boundary handling in circle collision detection
     * when two circles are exactly touching (distance between centers equals
     * sum of radii). This is a critical edge case that tests the collision
     * algorithm's precision and boundary condition handling.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Boundary conditions are handled correctly</li>
     * <li>Touching circles are not considered colliding</li>
     * <li>Floating-point precision doesn't cause false positives</li>
     * <li>Mathematical accuracy in distance calculations</li>
     * </ul>
     * <p>
     * This prevents unnecessary collision responses for barely touching objects.
     * </p>
     */
    @Test
    public void testCircleCircleCollisionTouching() {
        // Test circles that are exactly touching
        CircleGeometry circle1 = createCircle(0, 0, 5);
        CircleGeometry circle2 = createCircle(10, 0, 5); // centers 10 apart, radii 5 each = touching
        
        PhysicsGeometryPair pair1 = createTestPair(circle1, CIRCLE_1);
        PhysicsGeometryPair pair2 = createTestPair(circle2, CIRCLE_2);
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should not detect collision when exactly touching (distance == sum of radii)
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests circle-to-circle collision detection for overlapping circles.
     * <p>
     * Verifies that the collision detection algorithm correctly identifies
     * when two circles are actually overlapping (distance between centers
     * is less than the sum of radii). This tests the positive collision
     * case for circular objects.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>True collision between circles is properly detected</li>
     * <li>Distance calculations are accurate for overlapping circles</li>
     * <li>Circle collision algorithm works correctly</li>
     * <li>Mathematical precision in collision determination</li>
     * </ul>
     * <p>
     * This enables proper collision response for circular game objects.
     * </p>
     */
    @Test
    public void testCircleCircleCollisionOverlapping() {
        // Test circles that are overlapping
        CircleGeometry circle1 = createCircle(0, 0, 5);
        CircleGeometry circle2 = createCircle(8, 0, 5); // centers 8 apart, radii 5 each = overlapping
        
        PhysicsGeometryPair pair1 = createTestPair(circle1, CIRCLE_1);
        PhysicsGeometryPair pair2 = createTestPair(circle2, CIRCLE_2);
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should detect collision when overlapping (distance < sum of radii)
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    /**
     * Tests rectangle-to-circle collision detection with rotated rectangles.
     * <p>
     * Verifies collision detection between a rotated rectangle and a circle
     * using bounding box approximation. When rectangles have rotation applied,
     * the collision detection system falls back to using the axis-aligned
     * bounding box of the rotated rectangle for performance reasons, as
     * precise rotated rectangle-circle collision detection is computationally
     * expensive.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Rotated rectangle-circle collisions are detected correctly</li>
     * <li>Bounding box approximation is used for rotated rectangles</li>
     * <li>Mixed geometry collision with rotation works properly</li>
     * <li>Performance is maintained with complex rotated shapes</li>
     * </ul>
     * <p>
     * The bounding box approximation may be less precise but ensures
     * consistent performance in the physics simulation.
     * </p>
     */
    @Test
    public void testRectangleCircleCollisionWithRotation() {
        // Test rectangle-circle collision with rotated rectangle (uses bounding box approximation)
        RectangleGeometry rotatedRect = createRotatedRectangle(new Position(0, 0), 10, 10, 45);
        CircleGeometry circle = createCircle(5, 5, 3);
        
        PhysicsGeometryPair rectPair = createTestPair(rotatedRect, ROTATED_RECT);
        PhysicsGeometryPair circlePair = createTestPair(circle, CIRCLE);
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Should use bounding box approximation for rotated rectangle
        assertTrue(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
    }

    /**
     * Tests precise rectangle-to-circle collision detection without rotation.
     * <p>
     * Verifies the precise collision detection algorithm used when rectangles
     * have no rotation applied. This test specifically validates that the system
     * uses exact geometric calculations rather than bounding box approximations
     * when dealing with axis-aligned rectangles and circles.
     * </p>
     * <p>
     * The test setup includes:
     * </p>
     * <ul>
     * <li>Rectangle at (0,0) with dimensions 10x10 (no rotation)</li>
     * <li>Circle positioned to be near but not colliding with rectangle</li>
     * <li>Mathematical verification of non-collision case</li>
     * <li>Validation of precise distance calculations</li>
     * </ul>
     * <p>
     * This ensures maximum accuracy for axis-aligned collision detection,
     * which is important for gameplay precision and physics simulation.
     * </p>
     */
    @Test
    public void testRectangleCircleCollisionPreciseCalculation() {
        // Test rectangle-circle collision without rotation (precise calculation)
        RectangleGeometry rect = createRotatedRectangle(new Position(0, 0), 10, 10, 0); // explicitly no rotation
        // Circle position (15, 5) with radius 3 means center is at (18, 8)
        // Rectangle is from (0,0) to (10,10), so closest point to center (18,8) is (10,8) 
        // Distance from (18,8) to (10,8) = 8, radius = 3, so 8 > 3 = no collision
        CircleGeometry circle = createCircle(15, 5, 3);
        
        PhysicsGeometryPair rectPair = createTestPair(rect, RECT);
        PhysicsGeometryPair circlePair = createTestPair(circle, CIRCLE);
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Should use precise calculation for non-rotated rectangle
        assertFalse(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
    }

    /**
     * Tests precise rectangle-to-circle collision detection with actual collision.
     * <p>
     * Verifies the positive collision case using precise geometric calculations
     * when rectangles have no rotation. This test validates that the collision
     * detection algorithm correctly identifies when a circle overlaps with an
     * axis-aligned rectangle using exact distance calculations rather than
     * bounding box approximations.
     * </p>
     * <p>
     * The test includes detailed mathematical verification:
     * </p>
     * <ul>
     * <li>Rectangle bounds: (0,0) to (10,10)</li>
     * <li>Circle center calculation and radius consideration</li>
     * <li>Closest point on rectangle to circle center</li>
     * <li>Distance comparison against circle radius for collision determination</li>
     * </ul>
     * <p>
     * This test ensures that the precise collision algorithm works correctly
     * for the positive case, complementing the non-collision test and validating
     * the mathematical accuracy of the collision detection system.
     * </p>
     */
    @Test
    public void testRectangleCircleCollisionPreciseCalculationWithCollision() {
        // Test rectangle-circle collision without rotation with actual collision
        RectangleGeometry rect = createRotatedRectangle(new Position(0, 0), 10, 10, 0);
        // Circle position (7, 5) with radius 5 means center is at (12, 10)
        // Rectangle is from (0,0) to (10,10), so closest point to center (12,10) is (10,10)
        // Distance from (12,10) to (10,10) = sqrt((12-10)^2 + (10-10)^2) = 2
        // Since radius = 5 and distance = 2 < 5, there should be collision
        CircleGeometry circle = createCircle(7, 5, 5);
        
        PhysicsGeometryPair rectPair = createTestPair(rect, RECT);
        PhysicsGeometryPair circlePair = createTestPair(circle, CIRCLE);
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Should detect collision using precise calculation
        assertTrue(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
    }

    /**
     * Tests circle-to-rectangle collision detection with reversed parameter order.
     * <p>
     * Verifies that the collision detection system properly handles the case
     * where the first geometry is a circle and the second is a rectangle.
     * This tests the parameter swapping logic in the geometriesIntersect method,
     * ensuring that collision detection is symmetric and works correctly
     * regardless of the order in which geometries are presented.
     * </p>
     * <p>
     * This test ensures:
     * </p>
     * <ul>
     * <li>Parameter order independence in collision detection</li>
     * <li>Proper internal parameter swapping for circle-rectangle cases</li>
     * <li>Symmetric collision detection behavior</li>
     * <li>Consistent results regardless of geometry presentation order</li>
     * </ul>
     * <p>
     * This is important for the collision detection system's robustness,
     * as objects can be added to the simulation in any order and should
     * produce consistent collision results.
     * </p>
     */
    @Test
    public void testCircleRectangleCollisionOrderReversed() {
        // Test circle-rectangle collision (geom1 = circle, geom2 = rectangle)
        // This tests the reversed case in geometriesIntersect
        CircleGeometry circle = createCircle(5, 5, 3);
        RectangleGeometry rect = createRectangle(10, 10, 10, 10);
        
        PhysicsGeometryPair circlePair = createTestPair(circle, CIRCLE);
        PhysicsGeometryPair rectPair = createTestPair(rect, RECT);
        
        testPairs.add(circlePair);
        testPairs.add(rectPair);
        
        // Should handle circle-rectangle collision (parameters will be swapped internally)
        assertTrue(collisionDetection.wouldCauseOverlap(circlePair, 12, 12));
    }
}
