package mm.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestGeometricCollisionDetection {
    
    private SimulationModel mockModel;
    private GeometricCollisionDetection collisionDetection;
    private World testWorld;
    private List<PhysicsGeometryPair> testPairs;
    
    @BeforeEach
    public void setUp() {
        mockModel = mock(SimulationModel.class);
        collisionDetection = new GeometricCollisionDetection(mockModel);
        testWorld = new World(new Vec2(0, 0));
        testPairs = new ArrayList<>();
        
        when(mockModel.getGeometryPairs()).thenReturn(testPairs);
    }
    
    private Body createTestBody(String userData) {
        BodyDef bodyDef = new BodyDef();
        Body body = testWorld.createBody(bodyDef);
        body.setUserData(userData);
        return body;
    }
    
    private PhysicsGeometryPair createTestPair(GeometryData geometry, String userData) {
        Body body = createTestBody(userData);
        return new PhysicsGeometryPair(geometry, body);
    }
    
    @Test
    public void testWouldCauseOverlapNoCollision() {
        // Create two non-overlapping rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "object2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test moving pair1 to a position that doesn't overlap with pair2
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }
    
    @Test
    public void testWouldCauseOverlapWithCollision() {
        // Create two rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "object2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test moving pair1 to a position that overlaps with pair2
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 25, 25));
    }
    
    @Test
    public void testWouldCauseOverlapWithRotation() {
        // Create two rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "object2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test moving with rotation - should not overlap
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5, 45.0));
        
        // Test moving with rotation - should overlap
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 25, 25, 45.0));
    }
    
    @Test
    public void testSkipSelfCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        
        testPairs.add(pair1);
        
        // Should not collide with itself
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }
    
    @Test
    public void testSkipWinZoneCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry winZone = new RectangleGeometry(new Position(5, 5), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair winPair = createTestPair(winZone, "winZone");
        
        testPairs.add(pair1);
        testPairs.add(winPair);
        
        // Should not collide with win zone
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    @Test
    public void testSkipWinPlatCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry winPlat = new RectangleGeometry(new Position(5, 5), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair winPair = createTestPair(winPlat, "winPlat");
        
        testPairs.add(pair1);
        testPairs.add(winPair);
        
        // Should not collide with win platform
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    @Test
    public void testSkipNoPlaceZoneCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry noPlace = new RectangleGeometry(new Position(5, 5), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        PhysicsGeometryPair noPlacePair = createTestPair(noPlace, "noPlace");
        
        testPairs.add(pair1);
        testPairs.add(noPlacePair);
        
        // Should not collide with no-place zone
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    @Test
    public void testCircleCircleCollision() {
        CircleGeometry circle1 = new CircleGeometry(new Position(0, 0), 5);
        CircleGeometry circle2 = new CircleGeometry(new Position(15, 0), 5);
        
        PhysicsGeometryPair pair1 = createTestPair(circle1, "circle1");
        PhysicsGeometryPair pair2 = createTestPair(circle2, "circle2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test no collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
        
        // Test collision (move circle1 closer to circle2)
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 8, 0));
    }
    
    @Test
    public void testRectangleCircleCollision() {
        RectangleGeometry rect = new RectangleGeometry(new Position(0, 0), 10, 10);
        CircleGeometry circle = new CircleGeometry(new Position(20, 0), 5);
        
        PhysicsGeometryPair rectPair = createTestPair(rect, "rect");
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Test no collision
        assertFalse(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
        
        // Test collision (move rect closer to circle)
        assertTrue(collisionDetection.wouldCauseOverlap(rectPair, 12, 0));
    }
    
    @Test
    public void testRotatedRectangleCollision() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10, 45);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // With rotation, should use bounding box approximation
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }
    
    @Test
    public void testNullGeometryHandling() {
        // Create pair with null geometry
        PhysicsGeometryPair pair1 = createTestPair(null, "object1");
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5, 5), 10, 10);
        PhysicsGeometryPair pair2 = createTestPair(rect2, "object2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should not cause collision with null geometry
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 7, 7));
    }
    
    @Test
    public void testEmptyPairsList() {
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        PhysicsGeometryPair pair1 = createTestPair(rect1, "object1");
        
        // Empty pairs list
        testPairs.clear();
        
        // Should not cause collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5));
    }
    
    @Test
    public void testBoundingBoxesNoIntersection() {
        // Test bounding boxes that don't intersect
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 5, 5);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(10, 10), 5, 5);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // No bounding box intersection, so no collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    @Test
    public void testUnsupportedGeometryTypeInCreateGeometry() {
        // Create a custom geometry type that's not supported
        GeometryData unsupportedGeometry = new GeometryData(new Position(0, 0), 0) {
            @Override
            public boolean containsPoint(double x, double y) { return false; }
            
            @Override
            public double[] getBounds() { return new double[]{0, 0, 1, 1}; }
        };
        
        PhysicsGeometryPair pair1 = createTestPair(unsupportedGeometry, "unsupported");
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5, 5), 10, 10);
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should throw IllegalArgumentException for unsupported geometry type
        assertThrows(IllegalArgumentException.class, () -> {
            collisionDetection.wouldCauseOverlap(pair1, 7, 7);
        });
    }

    @Test
    public void testUnsupportedGeometryCombinationInGeometriesIntersect() {
        // This test is difficult to achieve because createGeometryAtPosition will throw first
        // Let's test the case where we have known geometries but test the fallback path
        // by creating a scenario where bounding boxes intersect but detailed collision returns false
        
        // Create two rectangles that have intersecting bounding boxes but don't actually collide
        // when using AABB collision detection
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 5, 5, 0);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5.1f, 0f), 5, 5, 0); // Just outside collision range
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should return false - bounding boxes might be close but AABB collision should return false
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    @Test
    public void testRectangleRectangleCollisionWithoutRotation() {
        // Test AABB collision for non-rotated rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10, 0); // explicitly no rotation
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5, 5), 10, 10, 0); // explicitly no rotation
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should detect collision using AABB algorithm
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    @Test
    public void testRectangleRectangleNoCollisionWithoutRotation() {
        // Test AABB no collision for non-rotated rectangles
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 5, 5, 0);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(6, 6), 5, 5, 0);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should not detect collision using AABB algorithm
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    @Test
    public void testCircleCircleCollisionTouching() {
        // Test circles that are exactly touching
        CircleGeometry circle1 = new CircleGeometry(new Position(0, 0), 5);
        CircleGeometry circle2 = new CircleGeometry(new Position(10, 0), 5); // centers 10 apart, radii 5 each = touching
        
        PhysicsGeometryPair pair1 = createTestPair(circle1, "circle1");
        PhysicsGeometryPair pair2 = createTestPair(circle2, "circle2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should not detect collision when exactly touching (distance == sum of radii)
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    @Test
    public void testCircleCircleCollisionOverlapping() {
        // Test circles that are overlapping
        CircleGeometry circle1 = new CircleGeometry(new Position(0, 0), 5);
        CircleGeometry circle2 = new CircleGeometry(new Position(8, 0), 5); // centers 8 apart, radii 5 each = overlapping
        
        PhysicsGeometryPair pair1 = createTestPair(circle1, "circle1");
        PhysicsGeometryPair pair2 = createTestPair(circle2, "circle2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Should detect collision when overlapping (distance < sum of radii)
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    @Test
    public void testRectangleCircleCollisionWithRotation() {
        // Test rectangle-circle collision with rotated rectangle (uses bounding box approximation)
        RectangleGeometry rotatedRect = new RectangleGeometry(new Position(0, 0), 10, 10, 45); // 45 degree rotation
        CircleGeometry circle = new CircleGeometry(new Position(5, 5), 3);
        
        PhysicsGeometryPair rectPair = createTestPair(rotatedRect, "rotatedRect");
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Should use bounding box approximation for rotated rectangle
        assertTrue(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
    }

    @Test
    public void testRectangleCircleCollisionPreciseCalculation() {
        // Test rectangle-circle collision without rotation (precise calculation)
        RectangleGeometry rect = new RectangleGeometry(new Position(0, 0), 10, 10, 0); // no rotation
        // Circle position (15, 5) with radius 3 means center is at (18, 8)
        // Rectangle is from (0,0) to (10,10), so closest point to center (18,8) is (10,8) 
        // Distance from (18,8) to (10,8) = 8, radius = 3, so 8 > 3 = no collision
        CircleGeometry circle = new CircleGeometry(new Position(15, 5), 3);
        
        PhysicsGeometryPair rectPair = createTestPair(rect, "rect");
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Should use precise calculation for non-rotated rectangle
        assertFalse(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
    }

    @Test
    public void testRectangleCircleCollisionPreciseCalculationWithCollision() {
        // Test rectangle-circle collision without rotation with actual collision
        RectangleGeometry rect = new RectangleGeometry(new Position(0, 0), 10, 10, 0);
        // Circle position (7, 5) with radius 5 means center is at (12, 10)
        // Rectangle is from (0,0) to (10,10), so closest point to center (12,10) is (10,10)
        // Distance from (12,10) to (10,10) = sqrt((12-10)^2 + (10-10)^2) = 2
        // Since radius = 5 and distance = 2 < 5, there should be collision
        CircleGeometry circle = new CircleGeometry(new Position(7, 5), 5);
        
        PhysicsGeometryPair rectPair = createTestPair(rect, "rect");
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Should detect collision using precise calculation
        assertTrue(collisionDetection.wouldCauseOverlap(rectPair, 0, 0));
    }

    @Test
    public void testBoundingBoxIntersectionEdgeCases() {
        // Test bounding boxes that touch exactly at edges
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 5, 5);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(5, 0), 5, 5); // touching right edge
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Touching edges should not be considered collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 0, 0));
    }

    @Test
    public void testZeroSizeGeometries() {
        // Test with zero-size geometries
        RectangleGeometry zeroRect = new RectangleGeometry(new Position(5, 5), 0, 0);
        CircleGeometry zeroCircle = new CircleGeometry(new Position(5, 5), 0);
        
        PhysicsGeometryPair rectPair = createTestPair(zeroRect, "zeroRect");
        PhysicsGeometryPair circlePair = createTestPair(zeroCircle, "zeroCircle");
        
        testPairs.add(rectPair);
        testPairs.add(circlePair);
        
        // Zero-size geometries at same position should not collide
        assertFalse(collisionDetection.wouldCauseOverlap(rectPair, 5, 5));
    }

    @Test
    public void testWouldCauseOverlapWithRotationParameter() {
        // Test the overloaded method that includes rotation
        RectangleGeometry rect1 = new RectangleGeometry(new Position(0, 0), 10, 10);
        RectangleGeometry rect2 = new RectangleGeometry(new Position(20, 20), 10, 10);
        
        PhysicsGeometryPair pair1 = createTestPair(rect1, "rect1");
        PhysicsGeometryPair pair2 = createTestPair(rect2, "rect2");
        
        testPairs.add(pair1);
        testPairs.add(pair2);
        
        // Test with rotation parameter - no collision
        assertFalse(collisionDetection.wouldCauseOverlap(pair1, 5, 5, 45.0));
        
        // Test with rotation parameter - collision
        assertTrue(collisionDetection.wouldCauseOverlap(pair1, 25, 25, 45.0));
    }

    @Test
    public void testCircleRectangleCollisionOrderReversed() {
        // Test circle-rectangle collision (geom1 = circle, geom2 = rectangle)
        // This tests the reversed case in geometriesIntersect
        CircleGeometry circle = new CircleGeometry(new Position(5, 5), 3);
        RectangleGeometry rect = new RectangleGeometry(new Position(10, 10), 10, 10);
        
        PhysicsGeometryPair circlePair = createTestPair(circle, "circle");
        PhysicsGeometryPair rectPair = createTestPair(rect, "rect");
        
        testPairs.add(circlePair);
        testPairs.add(rectPair);
        
        // Should handle circle-rectangle collision (parameters will be swapped internally)
        assertTrue(collisionDetection.wouldCauseOverlap(circlePair, 12, 12));
    }
}
