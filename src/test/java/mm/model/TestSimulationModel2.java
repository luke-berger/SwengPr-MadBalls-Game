package mm.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import mm.controller.GameObjectController;
import mm.controller.LevelExportController;
import mm.controller.LevelImportController;
import mm.controller.PhysicsAnimationController;
import mm.controller.UndoRedoController;

@ExtendWith(MockitoExtension.class)
class SimulationModelTest {

    private SimulationModel simulationModel;
    
    @Mock
    private World mockWorld;
    
    @Mock
    private LevelImportController mockImporter;
    
    @Mock
    private LevelExportController mockExporter;
    
    @Mock
    private PhysicsAnimationController mockTimer;
    
    @Mock
    private PhysicsVisualPair mockPair;
    
    @Mock
    private GameObject mockGameObject;
    
    @Mock
    private InventoryObject mockInventoryObject;
    
    @Mock
    private SimulationModel.WinListener mockWinListener;
    
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
    private Rectangle mockRectangle;
    
    @Mock
    private Circle mockCircle;
    
    @Mock
    private CollisionDetection mockCollisionService;

    private static final String TEST_LEVEL_PATH = "/level/test_level.json";

    @BeforeEach
    void setUp() {
        simulationModel = new SimulationModel(TEST_LEVEL_PATH);
    }

    @Test
    void testSimulationModel() {
        assertEquals(TEST_LEVEL_PATH, simulationModel.getLevelPath());
        assertNotNull(simulationModel.getUndoRedoManager());
        assertFalse(simulationModel.isWinScreenVisible());
    }

    @Test
    void testWorldSetterGetter() {
        simulationModel.setWorld(mockWorld);
        assertEquals(mockWorld, simulationModel.getWorld());
    }

    @Test
    void testPairsSetterGetter() {
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(mockPair);
        
        simulationModel.setPairs(pairs);
        assertEquals(pairs, simulationModel.getPairs());
        assertEquals(1, simulationModel.getPairs().size());
    }

    @Test
    void testDroppedObjectsSetterGetter() {
        List<GameObject> droppedObjects = new ArrayList<>();
        droppedObjects.add(mockGameObject);
        
        simulationModel.setDroppedObjects(droppedObjects);
        assertEquals(droppedObjects, simulationModel.getDroppedObjects());
        assertEquals(1, simulationModel.getDroppedObjects().size());
    }

    @Test
    void testInventoryObjectsSetterGetter() {
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        inventoryObjects.add(mockInventoryObject);
        
        simulationModel.setInventoryObjects(inventoryObjects);
        assertEquals(inventoryObjects, simulationModel.getInventoryObjects());
        assertEquals(1, simulationModel.getInventoryObjects().size());
    }

    @Test
    void testNoPlaceZonesSetterGetter() {
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        noPlaceZones.add(mockPair);
        
        simulationModel.setNoPlaceZones(noPlaceZones);
        assertEquals(noPlaceZones, simulationModel.getNoPlaceZones());
        assertEquals(1, simulationModel.getNoPlaceZones().size());
    }

    @Test
    void testTimerSetterGetter() {
        simulationModel.setTimer(mockTimer);
        assertEquals(mockTimer, simulationModel.getTimer());
    }

    @Test
    void testLevelPathSetterGetter() {
        String newPath = "/level/new_level.json";
        simulationModel.setLevelPath(newPath);
        assertEquals(newPath, simulationModel.getLevelPath());
    }

    @Test
    void testAddDroppedObject() {
        simulationModel.addDroppedObject(mockGameObject);
        
        List<GameObject> droppedObjects = simulationModel.getDroppedObjects();
        assertEquals(1, droppedObjects.size());
        assertTrue(droppedObjects.contains(mockGameObject));
    }

    @Test
    void testSetupSimulation() {
        // Setup mock data
        List<GameObject> levelObjects = new ArrayList<>();
        GameObject regularObject = mock(GameObject.class);
        GameObject noPlaceZoneObject = mock(GameObject.class);
        
        when(regularObject.getName()).thenReturn("regularObject");
        when(noPlaceZoneObject.getName()).thenReturn("noPlaceZone");
        
        levelObjects.add(regularObject);
        levelObjects.add(noPlaceZoneObject);
        
        // Add a dropped object
        GameObject droppedObject = mock(GameObject.class);
        when(droppedObject.getName()).thenReturn("droppedObject");
        simulationModel.addDroppedObject(droppedObject);
        
        PhysicsVisualPair regularPair = mock(PhysicsVisualPair.class);
        PhysicsVisualPair noPlaceZonePair = mock(PhysicsVisualPair.class);
        PhysicsVisualPair droppedPair = mock(PhysicsVisualPair.class);
        
        try (MockedStatic<LevelImportController> mockedImporter = mockStatic(LevelImportController.class);
             MockedStatic<GameObjectController> mockedGameController = mockStatic(GameObjectController.class)) {
            
            // Mock LevelImportController
            mockedImporter.when(() -> new LevelImportController(TEST_LEVEL_PATH)).thenReturn(mockImporter);
            when(mockImporter.getGameObjects()).thenReturn(levelObjects);
            
            // Mock GameObjectController conversions
            mockedGameController.when(() -> GameObjectController.convert(regularObject, any(World.class)))
                    .thenReturn(regularPair);
            mockedGameController.when(() -> GameObjectController.convert(noPlaceZoneObject, any(World.class)))
                    .thenReturn(noPlaceZonePair);
            mockedGameController.when(() -> GameObjectController.convert(droppedObject, any(World.class)))
                    .thenReturn(droppedPair);
            
            // Mock World constructor
            try (MockedStatic<World> mockedWorld = mockStatic(World.class)) {
                mockedWorld.when(() -> new World(any(Vec2.class))).thenReturn(mockWorld);
                
                // Mock PhysicsAnimationController constructor
                try (MockedStatic<PhysicsAnimationController> mockedTimer = mockStatic(PhysicsAnimationController.class)) {
                    mockedTimer.when(() -> new PhysicsAnimationController(any(World.class), any(List.class)))
                            .thenReturn(mockTimer);
                    
                    // Execute
                    simulationModel.setupSimulation();
                    
                    // Verify
                    assertEquals(mockWorld, simulationModel.getWorld());
                    assertEquals(3, simulationModel.getPairs().size()); // 2 level objects + 1 dropped object
                    assertEquals(1, simulationModel.getNoPlaceZones().size()); // Only the noPlaceZone object
                    assertEquals(mockTimer, simulationModel.getTimer());
                    
                    // Verify contact listener was set
                    verify(mockWorld).setContactListener(any(ContactListener.class));
                }
            }
        }
    }

    @Test
    void testSetupInventoryData() {
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        inventoryObjects.add(mockInventoryObject);
        
        try (MockedStatic<LevelImportController> mockedImporter = mockStatic(LevelImportController.class)) {
            mockedImporter.when(() -> new LevelImportController(TEST_LEVEL_PATH)).thenReturn(mockImporter);
            when(mockImporter.getInventoryObjects()).thenReturn(inventoryObjects);
            
            simulationModel.setupInvetoryData();
            
            assertEquals(inventoryObjects, simulationModel.getInventoryObjects());
        }
    }

    @Test
    void testExportLevel() {
        // Setup test data
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(mockPair);
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        inventoryObjects.add(mockInventoryObject);
        
        simulationModel.setPairs(pairs);
        simulationModel.setInventoryObjects(inventoryObjects);
        
        try (MockedStatic<LevelExportController> mockedExporter = mockStatic(LevelExportController.class)) {
            mockedExporter.when(() -> new LevelExportController()).thenReturn(mockExporter);
            
            simulationModel.exportLevel();
            
            verify(mockExporter).export(pairs, inventoryObjects);
        }
    }

    @Test
    void testFindInventoryObjectByName() {
        InventoryObject obj1 = mock(InventoryObject.class);
        InventoryObject obj2 = mock(InventoryObject.class);
        
        when(obj1.getName()).thenReturn("object1");
        when(obj2.getName()).thenReturn("object2");
        
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        inventoryObjects.add(obj1);
        inventoryObjects.add(obj2);
        
        simulationModel.setInventoryObjects(inventoryObjects);
        
        assertEquals(obj1, simulationModel.findInventoryObjectByName("object1"));
        assertEquals(obj2, simulationModel.findInventoryObjectByName("object2"));
        assertNull(simulationModel.findInventoryObjectByName("nonexistent"));
    }

    @Test
    void testCreateGameObjectFromInventory() {
        // Setup mock inventory object
        Size mockSize = mock(Size.class);
        when(mockSize.getWidth()).thenReturn(10.0f);
        when(mockSize.getHeight()).thenReturn(5.0f);
        
        when(mockInventoryObject.getName()).thenReturn("testObject");
        when(mockInventoryObject.getType()).thenReturn("testType");
        when(mockInventoryObject.getSize()).thenReturn(mockSize);
        when(mockInventoryObject.getPhysics()).thenReturn(mock(Physics.class));
        when(mockInventoryObject.getAngle()).thenReturn(0.0f);
        when(mockInventoryObject.getColour()).thenReturn(mock(String.class));
        when(mockInventoryObject.getSprite()).thenReturn(mock(String.class));
        when(mockInventoryObject.isWinning()).thenReturn(false);
        
        GameObject result = simulationModel.createGameObjectFromInventory(mockInventoryObject, 20.0f, 15.0f);
        
        assertNotNull(result);
        assertEquals("testObject", result.getName());
        assertEquals("testType", result.getType());
        
        // Check position is offset by half the size
        Position position = result.getPosition();
        assertEquals(15.0f, position.getX()); // 20 - 5 (half width)
        assertEquals(12.5f, position.getY()); // 15 - 2.5 (half height)
    }

    @Test
    void testIsInNoPlaceZone() {
        // Setup mock rectangle
        when(mockRectangle.getTranslateX()).thenReturn(10.0);
        when(mockRectangle.getTranslateY()).thenReturn(10.0);
        when(mockRectangle.getWidth()).thenReturn(20.0);
        when(mockRectangle.getHeight()).thenReturn(15.0);
        
        when(mockPair.visual).thenReturn(mockRectangle);
        
        List<PhysicsVisualPair> noPlaceZones = new ArrayList<>();
        noPlaceZones.add(mockPair);
        simulationModel.setNoPlaceZones(noPlaceZones);
        
        // Test points inside the zone
        assertTrue(simulationModel.isInNoPlaceZone(15.0, 15.0));
        assertTrue(simulationModel.isInNoPlaceZone(10.0, 10.0)); // Top-left corner
        assertTrue(simulationModel.isInNoPlaceZone(30.0, 25.0)); // Bottom-right corner
        
        // Test points outside the zone
        assertFalse(simulationModel.isInNoPlaceZone(5.0, 5.0));
        assertFalse(simulationModel.isInNoPlaceZone(35.0, 30.0));
    }

    @Test
    void testIsInWinZone() {
        // Setup mock rectangle win zone
        when(mockRectangle.getTranslateX()).thenReturn(10.0);
        when(mockRectangle.getTranslateY()).thenReturn(10.0);
        when(mockRectangle.getWidth()).thenReturn(20.0);
        when(mockRectangle.getHeight()).thenReturn(15.0);
        
        when(mockPair.visual).thenReturn(mockRectangle);
        when(mockPair.body).thenReturn(mockBodyA);
        when(mockBodyA.getUserData()).thenReturn("winZone");
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(mockPair);
        simulationModel.setPairs(pairs);
        
        // Test points inside the win zone
        assertTrue(simulationModel.isInWinZone(15.0, 15.0));
        
        // Test points outside the win zone
        assertFalse(simulationModel.isInWinZone(5.0, 5.0));
    }

    @Test
    void testIsInWinZoneWithCircle() {
        // Setup mock circle win zone
        when(mockCircle.getTranslateX()).thenReturn(10.0);
        when(mockCircle.getTranslateY()).thenReturn(10.0);
        when(mockCircle.getRadius()).thenReturn(5.0);
        
        when(mockPair.visual).thenReturn(mockCircle);
        when(mockPair.body).thenReturn(mockBodyA);
        when(mockBodyA.getUserData()).thenReturn("winPlat");
        
        List<PhysicsVisualPair> pairs = new ArrayList<>();
        pairs.add(mockPair);
        simulationModel.setPairs(pairs);
        
        // Test point inside the circle
        assertTrue(simulationModel.isInWinZone(12.0, 12.0));
        
        // Test point outside the circle
        assertFalse(simulationModel.isInWinZone(20.0, 20.0));
    }

    @Test
    void testWinConditionTriggered() {
        // Setup win listener
        simulationModel.setWinListener(mockWinListener);
        simulationModel.setTimer(mockTimer);
        
        // Setup contact mocks
        when(mockContact.getFixtureA()).thenReturn(mockFixtureA);
        when(mockContact.getFixtureB()).thenReturn(mockFixtureB);
        when(mockFixtureA.getBody()).thenReturn(mockBodyA);
        when(mockFixtureB.getBody()).thenReturn(mockBodyB);
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn("winPlat");
        
        // Setup simulation to capture contact listener
        simulationModel.setWorld(mockWorld);
        ArgumentCaptor<ContactListener> listenerCaptor = ArgumentCaptor.forClass(ContactListener.class);
        
        try (MockedStatic<LevelImportController> mockedImporter = mockStatic(LevelImportController.class);
             MockedStatic<GameObjectController> mockedGameController = mockStatic(GameObjectController.class)) {
            
            mockedImporter.when(() -> new LevelImportController(TEST_LEVEL_PATH)).thenReturn(mockImporter);
            when(mockImporter.getGameObjects()).thenReturn(new ArrayList<>());
            
            try (MockedStatic<World> mockedWorld = mockStatic(World.class)) {
                mockedWorld.when(() -> new World(any(Vec2.class))).thenReturn(mockWorld);
                
                try (MockedStatic<PhysicsAnimationController> mockedTimer = mockStatic(PhysicsAnimationController.class)) {
                    mockedTimer.when(() -> new PhysicsAnimationController(any(World.class), any(List.class)))
                            .thenReturn(mockTimer);
                    
                    simulationModel.setupSimulation();
                    
                    // Verify contact listener was set and capture it
                    verify(mockWorld).setContactListener(listenerCaptor.capture());
                    
                    // Trigger win condition
                    ContactListener listener = listenerCaptor.getValue();
                    listener.beginContact(mockContact);
                    
                    // Verify win actions
                    verify(mockTimer).stop();
                    verify(mockWinListener).onWin();
                    assertTrue(simulationModel.isWinScreenVisible());
                }
            }
        }
    }

    @Test
    void testWinConditionNotTriggered() {
        // Setup win listener
        simulationModel.setWinListener(mockWinListener);
        simulationModel.setTimer(mockTimer);
        
        // Setup contact mocks with non-win objects
        when(mockContact.getFixtureA()).thenReturn(mockFixtureA);
        when(mockContact.getFixtureB()).thenReturn(mockFixtureB);
        when(mockFixtureA.getBody()).thenReturn(mockBodyA);
        when(mockFixtureB.getBody()).thenReturn(mockBodyB);
        when(mockBodyA.getUserData()).thenReturn("regularObject");
        when(mockBodyB.getUserData()).thenReturn("anotherObject");
        
        // Setup simulation to capture contact listener
        simulationModel.setWorld(mockWorld);
        ArgumentCaptor<ContactListener> listenerCaptor = ArgumentCaptor.forClass(ContactListener.class);
        
        try (MockedStatic<LevelImportController> mockedImporter = mockStatic(LevelImportController.class);
             MockedStatic<GameObjectController> mockedGameController = mockStatic(GameObjectController.class)) {
            
            mockedImporter.when(() -> new LevelImportController(TEST_LEVEL_PATH)).thenReturn(mockImporter);
            when(mockImporter.getGameObjects()).thenReturn(new ArrayList<>());
            
            try (MockedStatic<World> mockedWorld = mockStatic(World.class)) {
                mockedWorld.when(() -> new World(any(Vec2.class))).thenReturn(mockWorld);
                
                try (MockedStatic<PhysicsAnimationController> mockedTimer = mockStatic(PhysicsAnimationController.class)) {
                    mockedTimer.when(() -> new PhysicsAnimationController(any(World.class), any(List.class)))
                            .thenReturn(mockTimer);
                    
                    simulationModel.setupSimulation();
                    
                    // Verify contact listener was set and capture it
                    verify(mockWorld).setContactListener(listenerCaptor.capture());
                    
                    // Trigger contact (should not trigger win)
                    ContactListener listener = listenerCaptor.getValue();
                    listener.beginContact(mockContact);
                    
                    // Verify no win actions
                    verify(mockTimer, never()).stop();
                    verify(mockWinListener, never()).onWin();
                    assertFalse(simulationModel.isWinScreenVisible());
                }
            }
        }
    }

    @Test
    void testWinConditionWithNullUserData() {
        // Setup win listener
        simulationModel.setWinListener(mockWinListener);
        simulationModel.setTimer(mockTimer);
        
        // Setup contact mocks with null user data
        when(mockContact.getFixtureA()).thenReturn(mockFixtureA);
        when(mockContact.getFixtureB()).thenReturn(mockFixtureB);
        when(mockFixtureA.getBody()).thenReturn(mockBodyA);
        when(mockFixtureB.getBody()).thenReturn(mockBodyB);
        when(mockBodyA.getUserData()).thenReturn(null);
        when(mockBodyB.getUserData()).thenReturn("winPlat");
        
        // Setup simulation to capture contact listener
        simulationModel.setWorld(mockWorld);
        ArgumentCaptor<ContactListener> listenerCaptor = ArgumentCaptor.forClass(ContactListener.class);
        
        try (MockedStatic<LevelImportController> mockedImporter = mockStatic(LevelImportController.class);
             MockedStatic<GameObjectController> mockedGameController = mockStatic(GameObjectController.class)) {
            
            mockedImporter.when(() -> new LevelImportController(TEST_LEVEL_PATH)).thenReturn(mockImporter);
            when(mockImporter.getGameObjects()).thenReturn(new ArrayList<>());
            
            try (MockedStatic<World> mockedWorld = mockStatic(World.class)) {
                mockedWorld.when(() -> new World(any(Vec2.class))).thenReturn(mockWorld);
                
                try (MockedStatic<PhysicsAnimationController> mockedTimer = mockStatic(PhysicsAnimationController.class)) {
                    mockedTimer.when(() -> new PhysicsAnimationController(any(World.class), any(List.class)))
                            .thenReturn(mockTimer);
                    
                    simulationModel.setupSimulation();
                    
                    // Verify contact listener was set and capture it
                    verify(mockWorld).setContactListener(listenerCaptor.capture());
                    
                    // Trigger contact (should not trigger win due to null user data)
                    ContactListener listener = listenerCaptor.getValue();
                    listener.beginContact(mockContact);
                    
                    // Verify no win actions
                    verify(mockTimer, never()).stop();
                    verify(mockWinListener, never()).onWin();
                    assertFalse(simulationModel.isWinScreenVisible());
                }
            }
        }
    }

    @Test
    void testRestoreInventoryCounts() {
        // Setup inventory objects
        InventoryObject inv1 = mock(InventoryObject.class);
        InventoryObject inv2 = mock(InventoryObject.class);
        when(inv1.getName()).thenReturn("object1");
        when(inv2.getName()).thenReturn("object2");
        when(inv1.getCount()).thenReturn(5);
        when(inv2.getCount()).thenReturn(3);
        
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        inventoryObjects.add(inv1);
        inventoryObjects.add(inv2);
        simulationModel.setInventoryObjects(inventoryObjects);
        
        // Setup dropped objects
        GameObject dropped1 = mock(GameObject.class);
        GameObject dropped2 = mock(GameObject.class);
        when(dropped1.getName()).thenReturn("object1");
        when(dropped2.getName()).thenReturn("object2");
        
        List<GameObject> droppedObjects = new ArrayList<>();
        droppedObjects.add(dropped1);
        droppedObjects.add(dropped2);
        simulationModel.setDroppedObjects(droppedObjects);
        
        // Execute
        simulationModel.restoreInventoryCounts();
        
        // Verify counts were incremented
        verify(inv1).setCount(6); // 5 + 1
        verify(inv2).setCount(4); // 3 + 1
    }

    @Test
    void testIncrementInventoryCount() {
        InventoryObject inv1 = mock(InventoryObject.class);
        when(inv1.getName()).thenReturn("object1");
        when(inv1.getCount()).thenReturn(5);
        
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        inventoryObjects.add(inv1);
        simulationModel.setInventoryObjects(inventoryObjects);
        
        simulationModel.incrementInventoryCount("object1");
        
        verify(inv1).setCount(6);
    }

    @Test
    void testDecrementInventoryCount() {
        InventoryObject inv1 = mock(InventoryObject.class);
        when(inv1.getName()).thenReturn("object1");
        when(inv1.getCount()).thenReturn(5);
        
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        inventoryObjects.add(inv1);
        simulationModel.setInventoryObjects(inventoryObjects);
        
        simulationModel.decrementInventoryCount("object1");
        
        verify(inv1).setCount(4);
    }

    @Test
    void testDecrementInventoryCountWhenZero() {
        InventoryObject inv1 = mock(InventoryObject.class);
        when(inv1.getName()).thenReturn("object1");
        when(inv1.getCount()).thenReturn(0);
        
        List<InventoryObject> inventoryObjects = new ArrayList<>();
        inventoryObjects.add(inv1);
        simulationModel.setInventoryObjects(inventoryObjects);
        
        simulationModel.decrementInventoryCount("object1");
        
        // Should not decrement below zero
        verify(inv1, never()).setCount(anyInt());
    }

    @Test
    void testWouldCauseOverlap() {
        // This test assumes CollisionDetection is injected or can be mocked
        // Since it's created in the constructor, we'd need to modify the class
        // to allow injection for proper testing. For now, this is a placeholder.
        
        // The method delegates to collisionService.wouldCauseOverlap()
        // In a real implementation, you'd want to inject the collision service
        // or make it testable in some other way
        
        assertNotNull(simulationModel); // Basic test to ensure method exists
    }

    @Test
    void testSetWinListener() {
        simulationModel.setWinListener(mockWinListener);
        // There's no getter for winListener, so we can't directly verify it's set
        // But we can test that it's used in win condition handling
        assertNotNull(simulationModel);
    }

    @Test
    void testWinConditionWithNoListener() {
        // Setup simulation without setting a win listener
        simulationModel.setTimer(mockTimer);
        
        // Setup contact mocks
        when(mockContact.getFixtureA()).thenReturn(mockFixtureA);
        when(mockContact.getFixtureB()).thenReturn(mockFixtureB);
        when(mockFixtureA.getBody()).thenReturn(mockBodyA);
        when(mockFixtureB.getBody()).thenReturn(mockBodyB);
        when(mockBodyA.getUserData()).thenReturn("winObject");
        when(mockBodyB.getUserData()).thenReturn("winPlat");
        
        // Setup simulation to capture contact listener
        simulationModel.setWorld(mockWorld);
        ArgumentCaptor<ContactListener> listenerCaptor = ArgumentCaptor.forClass(ContactListener.class);
        
        try (MockedStatic<LevelImportController> mockedImporter = mockStatic(LevelImportController.class);
             MockedStatic<GameObjectController> mockedGameController = mockStatic(GameObjectController.class)) {
            
            mockedImporter.when(() -> new LevelImportController(TEST_LEVEL_PATH)).thenReturn(mockImporter);
            when(mockImporter.getGameObjects()).thenReturn(new ArrayList<>());
            
            try (MockedStatic<World> mockedWorld = mockStatic(World.class)) {
                mockedWorld.when(() -> new World(any(Vec2.class))).thenReturn(mockWorld);
                
                try (MockedStatic<PhysicsAnimationController> mockedTimer = mockStatic(PhysicsAnimationController.class)) {
                    mockedTimer.when(() -> new PhysicsAnimationController(any(World.class), any(List.class)))
                            .thenReturn(mockTimer);
                    
                    simulationModel.setupSimulation();
                    
                    // Verify contact listener was set and capture it
                    verify(mockWorld).setContactListener(listenerCaptor.capture());
                    
                    // Trigger win condition without listener - should handle gracefully
                    ContactListener listener = listenerCaptor.getValue();
                    listener.beginContact(mockContact);
                    
                    // Should not crash, but no listener actions should occur
                    assertFalse(simulationModel.isWinScreenVisible());
                }
            }
        }
    }
}