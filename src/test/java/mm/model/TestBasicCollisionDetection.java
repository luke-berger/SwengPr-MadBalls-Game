package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests basic collision detection functionality.
 * <p>
 * This test class focuses on the fundamental collision detection operations
 * including basic overlap detection, rotation handling, and self-collision
 * prevention. These tests verify the core mechanics of the collision detection
 * system without special game logic or edge cases.
 * </p>
 * 
 * @see GeometricCollisionDetection
 * @see CollisionDetectionTestBase
 */
public class TestBasicCollisionDetection extends CollisionDetectionTestBase {
    
    /**
     * Tests the basic collision detection when objects should not collide.
     * <p>
     * Creates two non-overlapping rectangles positioned far apart and verifies
     * that the collision detection correctly identifies that moving one object
     * to a position that still doesn't overlap with the other returns false.
     * This is a fundamental test of the collision detection system's accuracy.
     * </p>
     * <p>
     * Test setup:
     * </p>
     * <ul>
     * <li>Rectangle 1: 10x10 at position (0, 0)</li>
     * <li>Rectangle 2: 10x10 at position (20, 20)</li>
     * <li>Test movement: Rectangle 1 to position (5, 5)</li>
     * </ul>
     */
    @Test
    public void testWouldCauseOverlapNoCollision() {
        // Create two non-overlapping rectangles
        RectangleGeometry rect1 = createRectangle(0, 0, 10, 10);
        RectangleGeometry rect2 = createRectangle(20, 20, 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, OBJECT_1);
        PhysicsGeometryPair pair2 = createTestPair(rect2, OBJECT_2);
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test moving pair1 to a position that doesn't overlap with pair2
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }
    
    /**
     * Tests collision detection when objects should collide.
     * <p>
     * Creates two rectangles and tests that the collision detection correctly
     * identifies when moving one object to a position that would cause it to
     * overlap with another object. This validates the positive collision
     * detection case.
     * </p>
     * <p>
     * Test setup:
     * </p>
     * <ul>
     * <li>Rectangle 1: 10x10 at position (0, 0)</li>
     * <li>Rectangle 2: 10x10 at position (20, 20)</li>
     * <li>Test movement: Rectangle 1 to position (25, 25) - should cause overlap</li>
     * </ul>
     */
    @Test
    public void testWouldCauseOverlapWithCollision() {
        // Create two rectangles
        RectangleGeometry rect1 = createRectangle(0, 0, 10, 10);
        RectangleGeometry rect2 = createRectangle(20, 20, 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, OBJECT_1);
        PhysicsGeometryPair pair2 = createTestPair(rect2, OBJECT_2);
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test moving pair1 to a position that overlaps with pair2
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 25, 25));
    }
    
    /**
     * Tests collision detection with rotation parameter.
     * <p>
     * Verifies that the overloaded wouldCauseOverlap method that includes a
     * rotation parameter works correctly for both collision and non-collision
     * scenarios. This tests the rotation-aware collision detection functionality.
     * </p>
     * <p>
     * The test covers:
     * </p>
     * <ul>
     * <li>Non-collision case with rotation applied</li>
     * <li>Collision case with rotation applied</li>
     * <li>Proper handling of the rotation parameter in collision calculations</li>
     * </ul>
     */
    @Test
    public void testWouldCauseOverlapWithRotation() {
        // Create two rectangles
        RectangleGeometry rect1 = createRectangle(0, 0, 10, 10);
        RectangleGeometry rect2 = createRectangle(20, 20, 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, OBJECT_1);
        PhysicsGeometryPair pair2 = createTestPair(rect2, OBJECT_2);
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test moving with rotation - should not overlap
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5, 45.0));
        
        // Test moving with rotation - should overlap
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 25, 25, 45.0));
    }
    
    /**
     * Tests that objects don't collide with themselves.
     * <p>
     * Verifies the collision detection system's ability to skip self-collision
     * checks. An object should never be considered to collide with itself,
     * which is essential for proper collision detection behavior in the
     * simulation where objects need to be moved without false positive
     * self-collisions.
     * </p>
     * <p>
     * This is a critical safety check in collision detection systems.
     * </p>
     */
    @Test
    public void testSkipSelfCollision() {
        RectangleGeometry rect1 = createRectangle(0, 0, 10, 10);
        PhysicsGeometryPair pair1 = createTestPair(rect1, OBJECT_1);
        
        testPairs.add(pair1);
        
        // Should not collide with itself
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }

    /**
     * Tests collision detection using the rotation parameter overload.
     * <p>
     * Verifies the overloaded wouldCauseOverlap method that accepts a rotation
     * parameter in addition to position coordinates. This method allows for
     * testing collision detection with objects that have rotation applied
     * during the collision check, which is useful for dynamic rotation
     * scenarios in the physics simulation.
     * </p>
     * <p>
     * Test scenarios covered:
     * </p>
     * <ul>
     * <li>Non-collision case with 45-degree rotation applied</li>
     * <li>Collision case with rotation applied and position change</li>
     * <li>Validation that rotation parameter is properly handled</li>
     * <li>Consistency with the non-rotation version of the method</li>
     * </ul>
     * <p>
     * This ensures the collision detection system can handle dynamic
     * rotations during collision queries, which is essential for
     * realistic physics simulation with rotating objects.
     * </p>
     */
    @Test
    public void testWouldCauseOverlapWithRotationParameter() {
        // Test the overloaded method that includes rotation
        RectangleGeometry rect1 = createRectangle(0, 0, 10, 10);
        RectangleGeometry rect2 = createRectangle(20, 20, 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, RECT_1);
        PhysicsGeometryPair pair2 = createTestPair(rect2, RECT_2);
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test with rotation parameter - no collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5, 45.0));
        
        // Test with rotation parameter - collision
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 25, 25, 45.0));
    }
}
