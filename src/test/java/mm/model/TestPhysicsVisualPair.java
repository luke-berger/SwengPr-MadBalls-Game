package mm.model;

import org.jbox2d.dynamics.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.junit.jupiter.api.Test;

public class TestPhysicsVisualPair {
    private World testWorld = new World(new Vec2(0.0f, -9.81f));
    @Test
    public void testConstructor() {
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(0, 0);
        
        Body testBody = testWorld.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.0f, 1.0f);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.1f;
        
        testBody.createFixture(fixtureDef);
        
        PhysicsVisualPair testPair = new PhysicsVisualPair(null, testBody);
        assertNotNull(testPair);
        assertNotNull(testPair.getClass());
        assertNull(testPair.visual);
        assertNull(testPair.getVisual());
        assertNotNull(testPair.body);
        assertNotNull(testPair.getBody());
        assertEquals(testBody, testPair.body);
        assertEquals(testBody, testPair.getBody());     
    }
}
