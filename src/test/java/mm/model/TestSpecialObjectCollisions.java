package mm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests collision detection behavior with special game objects.
 * <p>
 * This test class focuses on collision detection logic for special game objects
 * that have unique collision behavior, including win zones, win platforms, and
 * no-place zones. These objects typically allow other objects to pass through
 * them or have special interaction rules.
 * </p>
 * 
 * @see GeometricCollisionDetection
 * @see CollisionDetectionTestBase
 */
public class TestSpecialObjectCollisions extends CollisionDetectionTestBase {
    
    /**
     * Tests collision skipping for win zone objects.
     * <p>
     * Verifies that the collision detection system correctly skips collision
     * checks with objects marked as "winZone". Win zones are special game
     * objects that should not block object movement but instead trigger
     * victory conditions when objects enter them.
     * </p>
     * <p>
     * This test ensures that game objects can move through win zones without
     * being blocked by collision detection, which is essential for proper
     * game mechanics.
     * </p>
     */
    @Test
    public void testSkipWinZoneCollision() {
        RectangleGeometry rect1 = createRectangle(0, 0, 10, 10);
        RectangleGeometry winZone = createRectangle(5, 5, 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, OBJECT_1);
        PhysicsGeometryPair winPair = createTestPair(winZone, WIN_ZONE);
        
        testPairs.add(pair1);
        testPairs.add(winPair);
        
        // Should not collide with win zone
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    /**
     * Tests collision skipping for win platform objects.
     * <p>
     * Verifies that the collision detection system correctly skips collision
     * checks with objects marked as "winPlat". Win platforms are special game
     * objects similar to win zones that should allow objects to pass through
     * them without collision blocking, typically used for goal areas or
     * victory conditions.
     * </p>
     * <p>
     * This ensures proper game flow where objects can reach victory conditions
     * without being blocked by collision detection.
     * </p>
     */
    @Test
    public void testSkipWinPlatCollision() {
        RectangleGeometry rect1 = createRectangle(0, 0, 10, 10);
        RectangleGeometry winPlat = createRectangle(5, 5, 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, OBJECT_1);
        PhysicsGeometryPair winPair = createTestPair(winPlat, WIN_PLAT);
        
        testPairs.add(pair1);
        testPairs.add(winPair);
        
        // Should not collide with win platform
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    /**
     * Tests collision skipping for no-place zone objects.
     * <p>
     * Verifies that the collision detection system correctly skips collision
     * checks with objects marked as "noPlace". No-place zones are special game
     * objects that define areas where players cannot place objects, but existing
     * objects should be able to move through them freely.
     * </p>
     * <p>
     * This test ensures that no-place zones don't interfere with object
     * movement during gameplay, while still serving their purpose of
     * restricting object placement.
     * </p>
     */
    @Test
    public void testSkipNoPlaceZoneCollision() {
        RectangleGeometry rect1 = createRectangle(0, 0, 10, 10);
        RectangleGeometry noPlace = createRectangle(5, 5, 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, OBJECT_1);
        PhysicsGeometryPair noPlacePair = createTestPair(noPlace, NO_PLACE);
        
        testPairs.add(pair1);
        testPairs.add(noPlacePair);
        
        // Should not collide with no-place zone
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
}
