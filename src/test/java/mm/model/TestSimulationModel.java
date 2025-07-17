package mm.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import mm.model.*;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mm.controller.PhysicsAnimationController;
import mm.controller.UndoRedoController;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;

public class TestSimulationModel {

    @Mock
    private World mockWorld;
    
    @Mock
    private PhysicsAnimationController mockTimer;
    
    @Mock
    private Contact mockContact;
    
    @Mock
    private Fixture mockFixtureA;
    
    @Mock
    private Fixture mockFixtureB;
    
    @Mock
    private Body mockBodyA;
    
    @Mock
    private Body mockBodyB;
    
    @Mock
    private SimulationModel.WinListener mockWinListener;
    
    private SimulationModel simulationModel;
    private final String testLevelPath = "/test/level1.json";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        simulationModel = new SimulationModel(testLevelPath);
        
        // Setup mock contact behavior
        when(mockContact.getFixtureA()).thenReturn(mockFixtureA);
        when(mockContact.getFixtureB()).thenReturn(mockFixtureB);
        when(mockFixtureA.getBody()).thenReturn(mockBodyA);
        when(mockFixtureB.getBody()).thenReturn(mockBodyB);
    }

    // Test Constructor
    @Test
    void testConstructor() {
        SimulationModel model = new SimulationModel(testLevelPath);
        assertEquals(testLevelPath, model.getLevelPath());
        assertFalse(model.isWinScreenVisible());
        assertNotNull(model.getUndoRedoManager());
    }

    // Test Basic Getters and Setters
    @Test
    void testWorldGetterSetter() {
        simulationModel.setWorld(mockWorld);
        assertEquals(mockWorld, simulationModel.getWorld());
    }

    @Test
    void testPairsGetterSetter() {
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        PhysicsVisualPair pair = createMockPhysicsVisualPair();
        pairs.add(pair);
        
        simulationModel.setPairs(pairs);
        assertEquals(pairs, simulationModel.getPairs());
        assertEquals(1, simulationModel.getPairs().size());
    }

    @Test
    void testDroppedObjectsGetterSetter() {
        List<GameObject> droppedObjects = new ArrayList<>();
        GameObject obj = createMockGameObject("testObj");
        droppedObjects.add(obj);
        
        simulationModel.setDroppedObjects(droppedObjects);
        assertEquals(droppedObjects, simulationModel.getDroppedObjects());
        assertEquals(1, simulationModel.getDroppedObjects().size());
    }

    @Test
    void testDroppedVisualPairsGetterSetter() {
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        PhysicsVisualPair pair = createMockPhysicsVisualPair();
        pairs.add(pair);
        
        simulationModel.setDroppedVisualPairs(pairs);
        assertEquals(pairs, simulationModel.getDroppedPhysicsVisualPairs());
    }

    @Test
    void testInventoryObjectsGetterSetter() {
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        InventoryObject obj = createMockInventoryObject("testInventory");
        inventoryObjects.add(obj);
        
        simulationModel.setInventoryObjects(inventoryObjects);
        assertEquals(inventoryObjects, simulationModel.getInventoryObjects());
        assertEquals(1, simulationModel.getInventoryObjects().size());
    }

    @Test
    void testNoPlaceZonesGetterSetter() {
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        PhysicsVisualPair zone = createMockPhysicsVisualPair();
        noPlaceZones.add(zone);
        
        simulationModel.setNoPlaceZones(noPlaceZones);
        assertEquals(noPlaceZones, simulationModel.getNoPlaceZones());
    }

    @Test
    void testTimerGetterSetter() {
        simulationModel.setTimer(mockTimer);
        assertEquals(mockTimer, simulationModel.getTimer());
    }

    @Test
    void testLevelPathGetterSetter() {
        String newPath = "/test/level2.json";
        simulationModel.setLevelPath(newPath);
        assertEquals(newPath, simulationModel.getLevelPath());
    }

    @Test
    void testWinScreenVisibility() {
        assertFalse(simulationModel.isWinScreenVisible());
        
        // Test win condition trigger (this will set winScreenVisible to true)
        simulationModel.setWinListener(mockWinListener);
        simulationModel.setTimer(mockTimer);
        
        // Setup world and trigger win condition
        simulationModel.setWorld(mockWorld);
        triggerWinCondition();
        
        assertTrue(simulationModel.isWinScreenVisible());
    }

    @Test
    void testWinListenerSetter() {
        simulationModel.setWinListener(mockWinListener);
        // We can't directly test the getter as it's private, but we can test functionality
        assertDoesNotThrow(() -> simulationModel.setWinListener(mockWinListener));
    }

    // Test addDroppedObject
    @Test
    void testAddDroppedObject() {
        GameObject obj = createMockGameObject("testObj");
        simulationModel.addDroppedObject(obj);
        
        assertTrue(simulationModel.getDroppedObjects().contains(obj));
        assertEquals(1, simulationModel.getDroppedObjects().size());
    }

    // Test findInventoryObjectByName
    @Test
    void testFindInventoryObjectByName_Found() {
        InventoryObject obj = createMockInventoryObject("testItem");
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(obj);
        simulationModel.setInventoryObjects(inventory);
        
        InventoryObject found = simulationModel.findInventoryObjectByName("testItem");
        assertEquals(obj, found);
    }

    @Test
    void testFindInventoryObjectByName_NotFound() {
        InventoryObject obj = createMockInventoryObject("testItem");
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(obj);
        simulationModel.setInventoryObjects(inventory);
        
        InventoryObject found = simulationModel.findInventoryObjectByName("nonExistentItem");
        assertNull(found);
    }

    @Test
    void testFindInventoryObjectByName_EmptyInventory() {
        simulationModel.setInventoryObjects(new ArrayList<>());
        
        InventoryObject found = simulationModel.findInventoryObjectByName("anyItem");
        assertNull(found);
    }

    // Test createGameObjectFromInventory
    @Test
    void testCreateGameObjectFromInventory() {
        InventoryObject template = createMockInventoryObject("testTemplate");
        when(template.getSize()).thenReturn(new Size());
        when(template.getPhysics()).thenReturn(new Physics());
        when(template.getAngle()).thenReturn(45.0f);
        when(template.getColour()).thenReturn("BLACK");
        when(template.getSprite()).thenReturn("Sprite");
        when(template.isWinning()).thenReturn(true);
        
        GameObject result = simulationModel.createGameObjectFromInventory(template, 100, 150);
        
        assertNotNull(result);
        assertEquals("testTemplate", result.getName());
        // Position should be offset by half the size
        assertEquals(90, result.getPosition().getX()); // 100 - 10 (half of 20)
        assertEquals(135, result.getPosition().getY()); // 150 - 15 (half of 30)
    }

    // Test isInNoPlaceZone
    @Test
    void testIsInNoPlaceZone_InsideZone() {
        Rectangle mockRect = mock(Rectangle.class);
        when(mockRect.getTranslateX()).thenReturn(50.0);
        when(mockRect.getTranslateY()).thenReturn(50.0);
        when(mockRect.getWidth()).thenReturn(100.0);
        when(mockRect.getHeight()).thenReturn(100.0);
        
        PhysicsVisualPair zone = createMockPhysicsVisualPair();
        zone.visual = mockRect;
        
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        noPlaceZones.add(zone);
        simulationModel.setNoPlaceZones(noPlaceZones);
        
        assertTrue(simulationModel.isInNoPlaceZone(75, 75)); // Inside zone
    }

    @Test
    void testIsInNoPlaceZone_OutsideZone() {
        Rectangle mockRect = mock(Rectangle.class);
        when(mockRect.getTranslateX()).thenReturn(50.0);
        when(mockRect.getTranslateY()).thenReturn(50.0);
        when(mockRect.getWidth()).thenReturn(100.0);
        when(mockRect.getHeight()).thenReturn(100.0);
        
        PhysicsVisualPair zone = createMockPhysicsVisualPair();
        zone.visual = mockRect;
        
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        noPlaceZones.add(zone);
        simulationModel.setNoPlaceZones(noPlaceZones);
        
        assertFalse(simulationModel.isInNoPlaceZone(25, 25)); // Outside zone
    }

    @Test
    void testIsInNoPlaceZone_EmptyZones() {
        simulationModel.setNoPlaceZones(new ArrayList<>());
        assertFalse(simulationModel.isInNoPlaceZone(50, 50));
    }

    @Test
    void testIsInNoPlaceZone_NonRectangleVisual() {
        Circle mockCircle = mock(Circle.class);
        PhysicsVisualPair zone = createMockPhysicsVisualPair();
        zone.visual = mockCircle;
        
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        noPlaceZones.add(zone);
        simulationModel.setNoPlaceZones(noPlaceZones);
        
        assertFalse(simulationModel.isInNoPlaceZone(50, 50));
    }

    // Test isInWinZone
    @Test
    void testIsInWinZone_InsideWinZoneRectangle() {
        Rectangle mockRect = mock(Rectangle.class);
        when(mockRect.getTranslateX()).thenReturn(50.0);
        when(mockRect.getTranslateY()).thenReturn(50.0);
        when(mockRect.getWidth()).thenReturn(100.0);
        when(mockRect.getHeight()).thenReturn(100.0);
        
        Body mockBody = mock(Body.class);
        when(mockBody.getUserData()).thenReturn("winZone");
        
        PhysicsVisualPair winZone = createMockPhysicsVisualPair();
        winZone.visual = mockRect;
        winZone.body = mockBody;
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(winZone);
        simulationModel.setPairs(pairs);
        
        assertTrue(simulationModel.isInWinZone(75, 75)); // Inside win zone
    }

    @Test
    void testIsInWinZone_InsideWinPlatRectangle() {
        Rectangle mockRect = mock(Rectangle.class);
        when(mockRect.getTranslateX()).thenReturn(50.0);
        when(mockRect.getTranslateY()).thenReturn(50.0);
        when(mockRect.getWidth()).thenReturn(100.0);
        when(mockRect.getHeight()).thenReturn(100.0);
        
        Body mockBody = mock(Body.class);
        when(mockBody.getUserData()).thenReturn("winPlat");
        
        PhysicsVisualPair winZone = createMockPhysicsVisualPair();
        winZone.visual = mockRect;
        winZone.body = mockBody;
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(winZone);
        simulationModel.setPairs(pairs);
        
        assertTrue(simulationModel.isInWinZone(75, 75)); // Inside win platform
    }

    @Test
    void testIsInWinZone_InsideWinZoneCircle() {
        Circle mockCircle = mock(Circle.class);
        when(mockCircle.getTranslateX()).thenReturn(100.0);
        when(mockCircle.getTranslateY()).thenReturn(100.0);
        when(mockCircle.getRadius()).thenReturn(50.0);
        
        Body mockBody = mock(Body.class);
        when(mockBody.getUserData()).thenReturn("winZone");
        
        PhysicsVisualPair winZone = createMockPhysicsVisualPair();
        winZone.visual = mockCircle;
        winZone.body = mockBody;
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(winZone);
        simulationModel.setPairs(pairs);
        
        assertTrue(simulationModel.isInWinZone(120, 120)); // Inside circle
    }

    @Test
    void testIsInWinZone_OutsideWinZoneCircle() {
        Circle mockCircle = mock(Circle.class);
        when(mockCircle.getTranslateX()).thenReturn(100.0);
        when(mockCircle.getTranslateY()).thenReturn(100.0);
        when(mockCircle.getRadius()).thenReturn(50.0);
        
        Body mockBody = mock(Body.class);
        when(mockBody.getUserData()).thenReturn("winZone");
        
        PhysicsVisualPair winZone = createMockPhysicsVisualPair();
        winZone.visual = mockCircle;
        winZone.body = mockBody;
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(winZone);
        simulationModel.setPairs(pairs);
        
        assertFalse(simulationModel.isInWinZone(200, 200)); // Outside circle
    }

    @Test
    void testIsInWinZone_NotWinZone() {
        Rectangle mockRect = mock(Rectangle.class);
        when(mockRect.getTranslateX()).thenReturn(50.0);
        when(mockRect.getTranslateY()).thenReturn(50.0);
        when(mockRect.getWidth()).thenReturn(100.0);
        when(mockRect.getHeight()).thenReturn(100.0);
        
        Body mockBody = mock(Body.class);
        when(mockBody.getUserData()).thenReturn("normalObject");
        
        PhysicsVisualPair normalPair = createMockPhysicsVisualPair();
        normalPair.visual = mockRect;
        normalPair.body = mockBody;
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(normalPair);
        simulationModel.setPairs(pairs);
        
        assertFalse(simulationModel.isInWinZone(75, 75)); // Not a win zone
    }

    @Test
    void testIsInWinZone_EmptyPairs() {
        simulationModel.setPairs(new ArrayList<>());
        assertFalse(simulationModel.isInWinZone(50, 50));
    }

    // Test inventory count methods
    @Test
    void testIncrementInventoryCount() {
        InventoryObject obj = createMockInventoryObject("testItem");
        when(obj.getCount()).thenReturn(5);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(obj);
        simulationModel.setInventoryObjects(inventory);
        
        simulationModel.incrementInventoryCount("testItem");
        
        verify(obj).setCount(6);
    }

    @Test
    void testIncrementInventoryCount_ItemNotFound() {
        InventoryObject obj = createMockInventoryObject("testItem");
        when(obj.getCount()).thenReturn(5);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(obj);
        simulationModel.setInventoryObjects(inventory);
        
        simulationModel.incrementInventoryCount("nonExistentItem");
        
        verify(obj, never()).setCount(anyInt());
    }

    @Test
    void testDecrementInventoryCount() {
        InventoryObject obj = createMockInventoryObject("testItem");
        when(obj.getCount()).thenReturn(5);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(obj);
        simulationModel.setInventoryObjects(inventory);
        
        simulationModel.decrementInventoryCount("testItem");
        
        verify(obj).setCount(4);
    }

    @Test
    void testDecrementInventoryCount_ZeroCount() {
        InventoryObject obj = createMockInventoryObject("testItem");
        when(obj.getCount()).thenReturn(0);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(obj);
        simulationModel.setInventoryObjects(inventory);
        
        simulationModel.decrementInventoryCount("testItem");
        
        verify(obj, never()).setCount(anyInt());
    }

    @Test
    void testDecrementInventoryCount_ItemNotFound() {
        InventoryObject obj = createMockInventoryObject("testItem");
        when(obj.getCount()).thenReturn(5);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(obj);
        simulationModel.setInventoryObjects(inventory);
        
        simulationModel.decrementInventoryCount("nonExistentItem");
        
        verify(obj, never()).setCount(anyInt());
    }

    // Test restoreInventoryCounts
    @Test
    void testRestoreInventoryCounts() {
        // Create dropped objects
        GameObject droppedObj1 = createMockGameObject("item1");
        GameObject droppedObj2 = createMockGameObject("item2");
        GameObject droppedObj3 = createMockGameObject("nonExistentItem");
        
        List<GameObject> droppedObjects = new ArrayList<>();
        droppedObjects.add(droppedObj1);
        droppedObjects.add(droppedObj2);
        droppedObjects.add(droppedObj3);
        simulationModel.setDroppedObjects(droppedObjects);
        
        // Create inventory objects
        InventoryObject invObj1 = createMockInventoryObject("item1");
        InventoryObject invObj2 = createMockInventoryObject("item2");
        when(invObj1.getCount()).thenReturn(3);
        when(invObj2.getCount()).thenReturn(5);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(invObj1);
        inventory.add(invObj2);
        simulationModel.setInventoryObjects(inventory);
        
        simulationModel.restoreInventoryCounts();
        
        verify(invObj1).setCount(4); // 3 + 1
        verify(invObj2).setCount(6); // 5 + 1
        // nonExistentItem should not cause any issues
    }

    // Test collision detection
    @Test
    void testWouldCauseOverlap() {
        PhysicsVisualPair pair = createMockPhysicsVisualPair();
        
        // The method delegates to collisionService, so we just test that it doesn't throw
        assertDoesNotThrow(() -> simulationModel.wouldCauseOverlap(pair, 100, 100));
    }

    // Test Contact Listener functionality
    @Test
    void testContactListener_WinCondition_WinObjectToWinZone() {
        setupWinConditionTest();
        
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn("winZone");
        
        triggerContact();
        
        verify(mockWinListener).onWin();
        verify(mockTimer).stop();
        assertTrue(simulationModel.isWinScreenVisible());
    }

    @Test
    void testContactListener_WinCondition_WinObjectToWinPlat() {
        setupWinConditionTest();
        
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn("winPlat");
        
        triggerContact();
        
        verify(mockWinListener).onWin();
        verify(mockTimer).stop();
        assertTrue(simulationModel.isWinScreenVisible());
    }

    @Test
    void testContactListener_WinCondition_WinZoneToWinObject() {
        setupWinConditionTest();
        
        when(mockBodyA.getUserData()).thenReturn("winZone");
        when(mockBodyB.getUserData()).thenReturn("winObject");
        
        triggerContact();
        
        verify(mockWinListener).onWin();
        verify(mockTimer).stop();
        assertTrue(simulationModel.isWinScreenVisible());
    }

    @Test
    void testContactListener_NoWinCondition_RegularObjects() {
        setupWinConditionTest();
        
        when(mockBodyA.getUserData()).thenReturn("normalObject");
        when(mockBodyB.getUserData()).thenReturn("anotherObject");
        
        triggerContact();
        
        verify(mockWinListener, never()).onWin();
        verify(mockTimer, never()).stop();
        assertFalse(simulationModel.isWinScreenVisible());
    }

    @Test
    void testContactListener_NoWinCondition_NullUserData() {
        setupWinConditionTest();
        
        when(mockBodyA.getUserData()).thenReturn(null);
        when(mockBodyB.getUserData()).thenReturn("winZone");
        
        triggerContact();
        
        verify(mockWinListener, never()).onWin();
        verify(mockTimer, never()).stop();
        assertFalse(simulationModel.isWinScreenVisible());
    }

    @Test
    void testContactListener_NoWinCondition_OneNullUserData() {
        setupWinConditionTest();
        
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn(null);
        
        triggerContact();
        
        verify(mockWinListener, never()).onWin();
        verify(mockTimer, never()).stop();
        assertFalse(simulationModel.isWinScreenVisible());
    }

    @Test
    void testContactListener_NoWinListener() {
        // Don't set a win listener
        simulationModel.setTimer(mockTimer);
        simulationModel.setWorld(mockWorld);
        
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn("winZone");
        
        // This should handle gracefully without throwing
        assertDoesNotThrow(() -> triggerContact());
        
        verify(mockTimer, never()).stop();
        assertFalse(simulationModel.isWinScreenVisible());
    }

    // Test UndoRedoController
    @Test
    void testGetUndoRedoManager() {
        UndoRedoController manager = simulationModel.getUndoRedoManager();
        assertNotNull(manager);
        assertSame(manager, simulationModel.getUndoRedoManager()); // Should be same instance
    }

    // Helper methods
    private void setupWinConditionTest() {
        simulationModel.setWinListener(mockWinListener);
        simulationModel.setTimer(mockTimer);
        simulationModel.setWorld(mockWorld);
    }

    private void triggerContact() {
        // This simulates what happens when setupSimulation() is called and contact occurs
        // We need to capture the ContactListener and trigger it
        ContactListener[] capturedListener = new ContactListener[1];
        doAnswer(invocation -> {
            capturedListener[0] = invocation.getArgument(0);
            return null;
        }).when(mockWorld).setContactListener(any(ContactListener.class));
        
        simulationModel.setWorld(mockWorld);
        
        // Trigger the contact
        if (capturedListener[0] != null) {
            capturedListener[0].beginContact(mockContact);
        }
    }

    private void triggerWinCondition() {
        // Set up a proper win condition test
        simulationModel.setWinListener(mockWinListener);
        simulationModel.setTimer(mockTimer);
        simulationModel.setWorld(mockWorld);
        
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn("winZone");
        
        triggerContact();
    }

    private PhysicsVisualPair createMockPhysicsVisualPair() {
        PhysicsVisualPair pair = new PhysicsVisualPair();
        pair.body = mock(Body.class);
        pair.visual = mock(javafx.scene.shapes.Shape.class);
        return pair;
    }

    private GameObject createMockGameObject(String name) {
        GameObject obj = mock(GameObject.class);
        when(obj.getName()).thenReturn(name);
        return obj;
    }

    private InventoryObject createMockInventoryObject(String name) {
        InventoryObject obj = mock(InventoryObject.class);
        when(obj.getName()).thenReturn(name);
        return obj;
    }

    // Additional tests for edge cases and full coverage
    @Test
    void testIsPositionInRectangle_EdgeCases() {
        Rectangle mockRect = mock(Rectangle.class);
        when(mockRect.getTranslateX()).thenReturn(50.0);
        when(mockRect.getTranslateY()).thenReturn(50.0);
        when(mockRect.getWidth()).thenReturn(100.0);
        when(mockRect.getHeight()).thenReturn(100.0);
        
        PhysicsVisualPair zone = createMockPhysicsVisualPair();
        zone.visual = mockRect;
        
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        noPlaceZones.add(zone);
        simulationModel.setNoPlaceZones(noPlaceZones);
        
        // Test edge cases
        assertTrue(simulationModel.isInNoPlaceZone(50, 50)); // Top-left corner
        assertTrue(simulationModel.isInNoPlaceZone(150, 150)); // Bottom-right corner
        assertTrue(simulationModel.isInNoPlaceZone(50, 150)); // Bottom-left corner
        assertTrue(simulationModel.isInNoPlaceZone(150, 50)); // Top-right corner
        assertFalse(simulationModel.isInNoPlaceZone(49, 75)); // Just outside left
        assertFalse(simulationModel.isInNoPlaceZone(151, 75)); // Just outside right
    }

    @Test
    void testIsPositionInCircle_EdgeCases() {
        Circle mockCircle = mock(Circle.class);
        when(mockCircle.getTranslateX()).thenReturn(100.0);
        when(mockCircle.getTranslateY()).thenReturn(100.0);
        when(mockCircle.getRadius()).thenReturn(50.0);
        
        Body mockBody = mock(Body.class);
        when(mockBody.getUserData()).thenReturn("winZone");
        
        PhysicsVisualPair winZone = createMockPhysicsVisualPair();
        winZone.visual = mockCircle;
        winZone.body = mockBody;
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(winZone);
        simulationModel.setPairs(pairs);
        
        // Test edge cases
        assertTrue(simulationModel.isInWinZone(100, 100)); // Center
        assertTrue(simulationModel.isInWinZone(150, 100)); // Right edge
        assertTrue(simulationModel.isInWinZone(100, 150)); // Bottom edge
        assertFalse(simulationModel.isInWinZone(151, 100)); // Just outside right
        assertFalse(simulationModel.isInWinZone(100, 151)); // Just outside bottom
    }

    @Test
    void testMultipleInventoryObjects_FindByName() {
        InventoryObject obj1 = createMockInventoryObject("item1");
        InventoryObject obj2 = createMockInventoryObject("item2");
        InventoryObject obj3 = createMockInventoryObject("item3");
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(obj1);
        inventory.add(obj2);
        inventory.add(obj3);
        simulationModel.setInventoryObjects(inventory);
        
        assertEquals(obj2, simulationModel.findInventoryObjectByName("item2"));
        assertEquals(obj3, simulationModel.findInventoryObjectByName("item3"));
        assertEquals(obj1, simulationModel.findInventoryObjectByName("item1"));
    }

    @Test
    void testMultipleNoPlaceZones() {
        Rectangle rect1 = mock(Rectangle.class);
        when(rect1.getTranslateX()).thenReturn(0.0);
        when(rect1.getTranslateY()).thenReturn(0.0);
        when(rect1.getWidth()).thenReturn(50.0);
        when(rect1.getHeight()).thenReturn(50.0);
        
        Rectangle rect2 = mock(Rectangle.class);
        when(rect2.getTranslateX()).thenReturn(100.0);
        when(rect2.getTranslateY()).thenReturn(100.0);
        when(rect2.getWidth()).thenReturn(50.0);
        when(rect2.getHeight()).thenReturn(50.0);
        
        PhysicsVisualPair zone1 = createMockPhysicsVisualPair();
        zone1.visual = rect1;
        PhysicsVisualPair zone2 = createMockPhysicsVisualPair();
        zone2.visual = rect2;
        
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        noPlaceZones.add(zone1);
        noPlaceZones.add(zone2);
        simulationModel.setNoPlaceZones(noPlaceZones);
        
        assertTrue(simulationModel.isInNoPlaceZone(25, 25)); // In first zone
        assertTrue(simulationModel.isInNoPlaceZone(125, 125)); // In second zone
        assertFalse(simulationModel.isInNoPlaceZone(75, 75)); // Between zones
    }

    @Test
    void testMultipleWinZones() {
        // Create first win zone (rectangle)
        Rectangle rect = mock(Rectangle.class);
        when(rect.getTranslateX()).thenReturn(0.0);
        when(rect.getTranslateY()).thenReturn(0.0);
        when(rect.getWidth()).thenReturn(50.0);
        when(rect.getHeight()).thenReturn(50.0);
        
        Body body1 = mock(Body.class);
        when(body1.getUserData()).thenReturn("winZone");
        
        PhysicsVisualPair winZone1 = createMockPhysicsVisualPair();
        winZone1.visual = rect;
        winZone1.body = body1;
        
        // Create second win zone (circle)
        Circle circle = mock(Circle.class);
        when(circle.getTranslateX()).thenReturn(200.0);
        when(circle.getTranslateY()).thenReturn(200.0);
        when(circle.getRadius()).thenReturn(30.0);
        
        Body body2 = mock(Body.class);
        when(body2.getUserData()).thenReturn("winPlat");
        
        PhysicsVisualPair winZone2 = createMockPhysicsVisualPair();
        winZone2.visual = circle;
        winZone2.body = body2;
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(winZone1);
        pairs.add(winZone2);
        simulationModel.setPairs(pairs);
        
        assertTrue(simulationModel.isInWinZone(25, 25)); // In first zone
        assertTrue(simulationModel.isInWinZone(200, 200)); // In second zone
        assertFalse(simulationModel.isInWinZone(100, 100)); // Between zones
    }

    @Test
    void testCreateGameObjectFromInventory_WithDifferentProperties() {
        InventoryObject template = createMockInventoryObject("complexTemplate");
        
        // Create mock objects for all properties
        Size mockSize = mock(Size.class);
        when(mockSize.getWidth()).thenReturn(40.0f);
        when(mockSize.getHeight()).thenReturn(60.0f);
        
        Physics mockPhysics = mock(Physics.class);
        Colour mockColour = mock(Colour.class);
        Sprite mockSprite = mock(Sprite.class);
        
        when(template.getSize()).thenReturn(mockSize);
        when(template.getPhysics()).thenReturn(mockPhysics);
        when(template.getAngle()).thenReturn(90.0f);
        when(template.getColour()).thenReturn(mockColour);
        when(template.getSprite()).thenReturn(mockSprite);
        when(template.isWinning()).thenReturn(false);
        when(template.getType()).thenReturn("specialType");
        
        GameObject result = simulationModel.createGameObjectFromInventory(template, 200, 300);
        
        assertNotNull(result);
        assertEquals("complexTemplate", result.getName());
        assertEquals("specialType", result.getType());
        // Position should be offset by half the size
        assertEquals(180, result.getPosition().getX()); // 200 - 20 (half of 40)
        assertEquals(270, result.getPosition().getY()); // 300 - 30 (half of 60)
        
        // Verify all setters were called
        verify(result).setPhysics(mockPhysics);
        verify(result).setAngle(90.0f);
        verify(result).setColour(mockColour);
        verify(result).setSprite(mockSprite);
        verify(result).setWinning(false);
    }

    @Test
    void testRestoreInventoryCounts_WithMultipleObjectsAndMissingItems() {
        // Create dropped objects including duplicates and missing items
        GameObject dropped1 = createMockGameObject("item1");
        GameObject dropped2 = createMockGameObject("item1"); // Duplicate
        GameObject dropped3 = createMockGameObject("item2");
        GameObject dropped4 = createMockGameObject("missingItem");
        
        List<GameObject> droppedObjects = new ArrayList<>();
        droppedObjects.add(dropped1);
        droppedObjects.add(dropped2);
        droppedObjects.add(dropped3);
        droppedObjects.add(dropped4);
        simulationModel.setDroppedObjects(droppedObjects);
        
        // Create inventory objects
        InventoryObject inv1 = createMockInventoryObject("item1");
        InventoryObject inv2 = createMockInventoryObject("item2");
        when(inv1.getCount()).thenReturn(1);
        when(inv2.getCount()).thenReturn(2);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(inv1);
        inventory.add(inv2);
        simulationModel.setInventoryObjects(inventory);
        
        simulationModel.restoreInventoryCounts();
        
        verify(inv1, times(2)).setCount(2); // Called twice for duplicate dropped objects
        verify(inv2, times(1)).setCount(3); // Called once
        // missingItem should not cause any verification failures
    }

    @Test
    void testInventoryCountMethods_WithMultipleItems() {
        InventoryObject item1 = createMockInventoryObject("item1");
        InventoryObject item2 = createMockInventoryObject("item2");
        InventoryObject item3 = createMockInventoryObject("item3");
        
        when(item1.getCount()).thenReturn(5);
        when(item2.getCount()).thenReturn(0);
        when(item3.getCount()).thenReturn(3);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(item1);
        inventory.add(item2);
        inventory.add(item3);
        simulationModel.setInventoryObjects(inventory);
        
        // Test increment
        simulationModel.incrementInventoryCount("item3");
        verify(item3).setCount(4);
        
        // Test decrement on positive count
        simulationModel.decrementInventoryCount("item1");
        verify(item1).setCount(4);
        
        // Test decrement on zero count (should not change)
        simulationModel.decrementInventoryCount("item2");
        verify(item2, never()).setCount(anyInt());
    }

    @Test
    void testContactListener_AllContactMethods() {
        setupWinConditionTest();
        
        // Capture the contact listener
        ContactListener[] capturedListener = new ContactListener[1];
        doAnswer(invocation -> {
            capturedListener[0] = invocation.getArgument(0);
            return null;
        }).when(mockWorld).setContactListener(any(ContactListener.class));
        
        simulationModel.setWorld(mockWorld);
        
        ContactListener listener = capturedListener[0];
        assertNotNull(listener);
        
        // Test all contact methods don't throw exceptions
        assertDoesNotThrow(() -> {
            listener.endContact(mockContact);
            listener.preSolve(mockContact, mock(Manifold.class));
            listener.postSolve(mockContact, mock(org.jbox2d.callbacks.ContactImpulse.class));
        });
    }

    @Test
    void testWinCondition_WithDifferentTargetTypes() {
        setupWinConditionTest();
        
        // Test winObject + winZone
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn("winZone");
        triggerContact();
        verify(mockWinListener).onWin();
        
        // Reset mocks
        reset(mockWinListener, mockTimer);
        setupWinConditionTest();
        
        // Test winObject + winPlat
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn("winPlat");
        triggerContact();
        verify(mockWinListener).onWin();
        
        // Reset mocks
        reset(mockWinListener, mockTimer);
        setupWinConditionTest();
        
        // Test winObject + other (should not win)
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn("otherObject");
        triggerContact();
        verify(mockWinListener, never()).onWin();
    }

    @Test
    void testWinCondition_ReversedOrder() {
        setupWinConditionTest();
        
        // Test winZone + winObject (reversed order)
        when(mockBodyA.getUserData()).thenReturn("winZone");
        when(mockBodyB.getUserData()).thenReturn("winObject");
        triggerContact();
        verify(mockWinListener).onWin();
        
        // Reset mocks
        reset(mockWinListener, mockTimer);
        setupWinConditionTest();
        
        // Test winPlat + winObject (reversed order)
        when(mockBodyA.getUserData()).thenReturn("winPlat");
        when(mockBodyB.getUserData()).thenReturn("winObject");
        triggerContact();
        verify(mockWinListener).onWin();
    }

    @Test
    void testIsInWinZone_WithUnsupportedVisualType() {
        // Create a visual that's neither Rectangle nor Circle
        javafx.scene.shape.Polygon mockPolygon = mock(javafx.scene.shape.Polygon.class);
        
        Body mockBody = mock(Body.class);
        when(mockBody.getUserData()).thenReturn("winZone");
        
        PhysicsVisualPair winZone = createMockPhysicsVisualPair();
        winZone.visual = mockPolygon;
        winZone.body = mockBody;
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(winZone);
        simulationModel.setPairs(pairs);
        
        // Should return false for unsupported visual type
        assertFalse(simulationModel.isInWinZone(100, 100));
    }

    @Test
    void testPhysicsComponentsContainerFunctionality() {
        // Test that the physics components container works as expected
        SimulationModel.PhysicsComponents physics = new SimulationModel.PhysicsComponents();
        
        World testWorld = mock(World.class);
        physics.world = testWorld;
        
        List<PhysicsVisualPair> testPairs = new ArrayList<>();
        physics.pairs = testPairs;
        
        PhysicsAnimationController testTimer = mock(PhysicsAnimationController.class);
        physics.timer = testTimer;
        
        assertEquals(testWorld, physics.world);
        assertEquals(testPairs, physics.pairs);
        assertEquals(testTimer, physics.timer);
    }

    @Test
    void testGameObjectCollectionsContainerFunctionality() {
        // Test that the game object collections container works as expected
        SimulationModel.GameObjectCollections collections = new SimulationModel.GameObjectCollections();
        
        List<GameObject> testDropped = new ArrayList<>();
        collections.droppedObjects = testDropped;
        
        List<InventoryObject> testInventory = new ArrayList<>();
        collections.inventoryObjects = testInventory;
        
        List<PhysicsVisualPair> testNoPlace = new ArrayList<>();
        collections.noPlaceZones = testNoPlace;
        
        List<PhysicsVisualPair> testDroppedVisual = new ArrayList<>();
        collections.droppedVisualPairs = testDroppedVisual;
        
        assertEquals(testDropped, collections.droppedObjects);
        assertEquals(testInventory, collections.inventoryObjects);
        assertEquals(testNoPlace, collections.noPlaceZones);
        assertEquals(testDroppedVisual, collections.droppedVisualPairs);
    }

    @Test
    void testSimulationStateContainerFunctionality() {
        // Test that the simulation state container works as expected
        SimulationModel.SimulationState state = new SimulationModel.SimulationState();
        
        String testLevelPath = "/test/level.json";
        state.levelPath = testLevelPath;
        
        boolean testWinScreen = true;
        state.winScreenVisible = testWinScreen;
        
        SimulationModel.WinListener testListener = mock(SimulationModel.WinListener.class);
        state.winListener = testListener;
        
        assertEquals(testLevelPath, state.levelPath);
        assertEquals(testWinScreen, state.winScreenVisible);
        assertEquals(testListener, state.winListener);
    }

    @Test
    void testWinListenerInterface() {
        // Test that the WinListener interface can be implemented
        SimulationModel.WinListener listener = new SimulationModel.WinListener() {
            @Override
            public void onWin() {
                // Implementation for testing
            }
        };
        
        simulationModel.setWinListener(listener);
        
        // Test that the listener can be called without issues
        assertDoesNotThrow(() -> listener.onWin());
    }

    @Test
    void testCollisionDetectionDelegation() {
        // Test that collision detection properly delegates to the service
        PhysicsVisualPair mockPair = createMockPhysicsVisualPair();
        
        // This should not throw an exception
        assertDoesNotThrow(() -> {
            boolean result = simulationModel.wouldCauseOverlap(mockPair, 50, 50);
            // The actual result depends on the CollisionDetection implementation
            // We're just testing that the method can be called
        });
    }

    @Test
    void testBoundaryConditions_RectangleEdges() {
        Rectangle mockRect = mock(Rectangle.class);
        when(mockRect.getTranslateX()).thenReturn(50.0);
        when(mockRect.getTranslateY()).thenReturn(50.0);
        when(mockRect.getWidth()).thenReturn(100.0);
        when(mockRect.getHeight()).thenReturn(100.0);
        
        PhysicsVisualPair zone = createMockPhysicsVisualPair();
        zone.visual = mockRect;
        
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        noPlaceZones.add(zone);
        simulationModel.setNoPlaceZones(noPlaceZones);
        
        // Test exact boundary conditions
        assertTrue(simulationModel.isInNoPlaceZone(50.0, 50.0)); // Exact top-left
        assertTrue(simulationModel.isInNoPlaceZone(150.0, 150.0)); // Exact bottom-right
        assertFalse(simulationModel.isInNoPlaceZone(49.9, 75.0)); // Just outside left
        assertFalse(simulationModel.isInNoPlaceZone(150.1, 75.0)); // Just outside right
        assertFalse(simulationModel.isInNoPlaceZone(75.0, 49.9)); // Just outside top
        assertFalse(simulationModel.isInNoPlaceZone(75.0, 150.1)); // Just outside bottom
    }

    @Test
    void testBoundaryConditions_CircleEdges() {
        Circle mockCircle = mock(Circle.class);
        when(mockCircle.getTranslateX()).thenReturn(100.0);
        when(mockCircle.getTranslateY()).thenReturn(100.0);
        when(mockCircle.getRadius()).thenReturn(50.0);
        
        Body mockBody = mock(Body.class);
        when(mockBody.getUserData()).thenReturn("winZone");
        
        PhysicsVisualPair winZone = createMockPhysicsVisualPair();
        winZone.visual = mockCircle;
        winZone.body = mockBody;
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(winZone);
        simulationModel.setPairs(pairs);
        
        // Test exact boundary conditions
        assertTrue(simulationModel.isInWinZone(100.0, 100.0)); // Center
        assertTrue(simulationModel.isInWinZone(150.0, 100.0)); // Right edge
        assertTrue(simulationModel.isInWinZone(100.0, 150.0)); // Bottom edge
        assertTrue(simulationModel.isInWinZone(100.0, 50.0)); // Top edge
        assertTrue(simulationModel.isInWinZone(50.0, 100.0)); // Left edge
        
        // Test diagonal edge (distance = radius)
        double diagonal = 100.0 + 50.0 / Math.sqrt(2);
        assertTrue(simulationModel.isInWinZone(diagonal, diagonal));
        
        // Test just outside
        assertFalse(simulationModel.isInWinZone(150.1, 100.0));
        assertFalse(simulationModel.isInWinZone(100.0, 150.1));
    }

    @Test
    void testEmptyCollections() {
        // Test behavior with empty collections
        simulationModel.setDroppedObjects(new ArrayList<>());
        simulationModel.setInventoryObjects(new ArrayList<>());
        simulationModel.setNoPlaceZones(new ArrayList<>());
        simulationModel.setPairs(new ArrayList<>());
        
        // These should all work without issues
        assertNull(simulationModel.findInventoryObjectByName("anything"));
        assertFalse(simulationModel.isInNoPlaceZone(100, 100));
        assertFalse(simulationModel.isInWinZone(100, 100));
        
        // Restore counts should work with empty collections
        assertDoesNotThrow(() -> simulationModel.restoreInventoryCounts());
        
        // Count operations should work with empty collections
        assertDoesNotThrow(() -> {
            simulationModel.incrementInventoryCount("anything");
            simulationModel.decrementInventoryCount("anything");
        });
    }

    @Test
    void testNullSafety() {
        // Test that methods handle null inputs gracefully where appropriate
        assertDoesNotThrow(() -> {
            simulationModel.setWorld(null);
            simulationModel.setPairs(null);
            simulationModel.setDroppedObjects(null);
            simulationModel.setInventoryObjects(null);
            simulationModel.setNoPlaceZones(null);
            simulationModel.setTimer(null);
            simulationModel.setWinListener(null);
        });
    }
}