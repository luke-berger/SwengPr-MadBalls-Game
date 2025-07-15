package mm.model;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.junit.jupiter.api.Test;

import mm.controller.PhysicsAnimationController;
import mm.model.SimulationModel.GameObjectCollections;
import mm.model.SimulationModel.PhysicsComponents;
import mm.model.SimulationModel.SimulationState;

public class TestSimulationModel {
    @Test
    public void testSimulationModelConstructor() {
        String testString = "test!";
        SimulationModel testModel = new SimulationModel(testString);
        assertNotNull(testModel);
        assertNotNull(testModel.getUndoRedoManager());
        //absicht, dass eskeine getter Setter für andere Final attribute gibt? 
        assertNotNull(testModel.getLevelPath());
        assertEquals(SimulationModel.class, testModel.getClass());
    }

    @Test
    public void testPhysicsVisualPairGetterSetter() {
        String testString = "test!";
        SimulationModel testModel = new SimulationModel(testString);
        assertEquals(testString, testModel.getLevelPath());

        World testWorld = new World(new Vec2(0.0f, -9.81f));
        testModel.setWorld(testWorld);
        assertEquals(testWorld, testModel.getWorld());

        PhysicsVisualPair testPair = generateTestPair(testWorld);
        List<PhysicsVisualPair> testPairs = new ArrayList<>();
        testPairs.add(testPair);
        testModel.setPairs(testPairs);
        testModel.setDroppedVisualPairs(testPairs);
        assertEquals(testPairs, testModel.getPairs());
        assertEquals(testPairs, testModel.getDroppedPhysicsVisualPairs());

        GameObject testGameObj = new GameObject();
        List<GameObject> testDroppedObjects= new ArrayList<>();
        testDroppedObjects.add(testGameObj);
        testModel.setDroppedObjects(testDroppedObjects);
        assertEquals(testDroppedObjects, testModel.getDroppedObjects());
        testModel.getDroppedObjects().clear();
        testModel.addDroppedObject(testGameObj);
        assertEquals(testGameObj, testModel.getDroppedObjects().get(0));

    }

    @Test 
    public void testPhysicsComponent() {
        PhysicsComponents testComponents = new PhysicsComponents();
        assertNotNull(testComponents);
        assertNotNull(testComponents.getClass());
        World testWorld = new World(new Vec2(0.0f, -9.81f));
        testComponents.world = testWorld;
        PhysicsVisualPair testPair = generateTestPair(testWorld);
        testComponents.pairs.add(testPair);
        PhysicsAnimationController testTimer = new PhysicsAnimationController(testWorld, testComponents.pairs);
        testComponents.timer = testTimer;
        assertEquals(testWorld, testComponents.world);
        assertEquals(testPair, testComponents.pairs.get(0));
        assertEquals(testTimer, testComponents.timer);
    }

    @Test
    public void testGameObjectCollections() {
        GameObjectCollections testCollections = new GameObjectCollections();
        assertNotNull(testCollections);
        assertNotNull(testCollections.getClass());
        GameObject testGameObj = new GameObject();
        testCollections.droppedObjects.add(testGameObj);
        InventoryObject testInventoryObj = new InventoryObject();
        testCollections.inventoryObjects.add(testInventoryObj);
        World testWorld = new World(new Vec2(0.0f, -9.81f));
        PhysicsVisualPair testPair = generateTestPair(testWorld);
        testCollections.droppedVisualPairs.add(testPair);
        assertEquals(1, testCollections.droppedObjects.size());
        assertEquals(testGameObj, testCollections.droppedObjects.get(0));
        assertEquals(1, testCollections.inventoryObjects.size());
        assertEquals(testInventoryObj, testCollections.inventoryObjects.get(0));
        assertEquals(0, testCollections.noPlaceZones.size());
        assertEquals(1, testCollections.droppedVisualPairs.size());
        assertEquals(testPair, testCollections.droppedVisualPairs.get(0));
    }

    @Test 
    public void testSimulationState() {
        SimulationState testState = new SimulationState();
        assertNotNull(testState);
        assertNotNull(testState.getClass());
        assertFalse(testState.winScreenVisible);
        String testString = "test 123";
        testState.levelPath = testString;
        testState.winScreenVisible = true;
        assertTrue(testState.winScreenVisible);
        assertNull(testState.winListener);
    }

    private PhysicsVisualPair generateTestPair(World testWorld) {
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
        return testPair;
    }
}
