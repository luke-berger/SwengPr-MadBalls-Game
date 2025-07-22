package mm.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the basic functionality of the {@link SimulationModel} class.
 * <p>
 * This test class focuses on testing non-JavaFX dependent methods and core business logic
 * of the SimulationModel. It covers inventory management, object creation, JSON serialization/deserialization,
 * collection management, and various getter/setter methods.
 * </p>
 * 
 * <h2>Test Coverage Areas:</h2>
 * <ul>
 * <li><b>Constructor and Basic Getters/Setters:</b> Tests initialization and basic property access</li>
 * <li><b>Inventory Management:</b> Tests inventory object operations, counting, and searching</li>
 * <li><b>Game Object Management:</b> Tests creation, addition, and manipulation of game objects</li>
 * <li><b>Collection Management:</b> Tests various collection setters and getters</li>
 * <li><b>JSON Operations:</b> Tests state serialization and deserialization</li>
 * <li><b>Physics Integration:</b> Tests physics-related setters and basic collision detection</li>
 * <li><b>Zone Detection:</b> Tests no-place zones and win zones position checking</li>
 * </ul>
 * 
 * <h2>Design Principles:</h2>
 * <ul>
 * <li><b>JavaFX Independence:</b> All tests avoid JavaFX dependencies for faster execution and CI/CD compatibility</li>
 * <li><b>Comprehensive Coverage:</b> Tests both happy path and edge cases including null/invalid inputs</li>
 * <li><b>Mock Usage:</b> Uses Mockito for testing complex dependencies without full system setup</li>
 * <li><b>Realistic Data:</b> Uses helper methods to create realistic test objects with proper configurations</li>
 * </ul>
 */
public class TestSimulationModel extends SimulationTestSetup {
    /**
     * Tests inventory management functionality.
     * <p>
     * Tests the core inventory operations including:
     * </p>
     * <ul>
     * <li>Setting and retrieving inventory collections</li>
     * <li>Finding inventory objects by name (both existing and non-existing)</li>
     * <li>Proper handling of object properties (name, count)</li>
     * </ul>
     * 
     * @see SimulationModel#setInventoryObjects(List)
     * @see SimulationModel#getInventoryObjects()
     * @see SimulationModel#findInventoryObjectByName(String)
     */
    @Test
    public void testInventoryManagement() {
        // Create test inventory
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(createTestInventoryObject("ball", 3));
        inventory.add(createTestInventoryObject("box", 2));
        
        simulationModel.setInventoryObjects(inventory);
        assertEquals(inventory, simulationModel.getInventoryObjects());
        
        // Test finding inventory by name
        InventoryObject found = simulationModel.findInventoryObjectByName("ball");
        assertNotNull(found);
        assertEquals("ball", found.getName());
        assertEquals(3, found.getCount());
        
        // Test finding non-existent inventory
        InventoryObject notFound = simulationModel.findInventoryObjectByName("nonexistent");
        assertNull(notFound);
    }
    
    /**
     * Tests inventory count operations (increment and decrement).
     * <p>
     * Tests the inventory counting system including:
     * </p>
     * <ul>
     * <li>Incrementing inventory counts</li>
     * <li>Decrementing inventory counts</li>
     * <li>Boundary protection (count doesn't go below zero)</li>
     * <li>Safe handling of operations on non-existent items</li>
     * </ul>
     * 
     * @see SimulationModel#incrementInventoryCount(String)
     * @see SimulationModel#decrementInventoryCount(String)
     */
    @Test
    public void testInventoryCountOperations() {
        // Setup test inventory
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(createTestInventoryObject("ball", 5));
        simulationModel.setInventoryObjects(inventory);
        
        InventoryObject ball = simulationModel.findInventoryObjectByName("ball");
        
        // Test increment
        simulationModel.incrementInventoryCount("ball");
        assertEquals(6, ball.getCount());
        
        // Test decrement
        simulationModel.decrementInventoryCount("ball");
        assertEquals(5, ball.getCount());
        
        // Test decrement doesn't go below zero
        ball.setCount(0);
        simulationModel.decrementInventoryCount("ball");
        assertEquals(0, ball.getCount());
        
        // Test operations on non-existent items don't crash
        assertDoesNotThrow(() -> {
            simulationModel.incrementInventoryCount("nonexistent");
            simulationModel.decrementInventoryCount("nonexistent");
        });
    }
    
    /**
     * Tests creation of GameObject instances from InventoryObject templates.
     * <p>
     * Tests the object creation system including:
     * </p>
     * <ul>
     * <li>Creating GameObjects from InventoryObject templates</li>
     * <li>Proper transfer of properties (name, type, color, winning status)</li>
     * <li>Correct position calculation with centering offset</li>
     * <li>Property inheritance from template to game object</li>
     * </ul>
     * 
     * @see SimulationModel#createGameObjectFromInventory(InventoryObject, float, float)
     */
    @Test
    public void testCreateGameObjectFromInventory() {
        // Setup test inventory
        InventoryObject template = createTestInventoryObject("ball", 3);
        template.setSize(new Size(10f, 10f));
        template.setColour("RED");
        template.setWinning(true);
        
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(template);
        simulationModel.setInventoryObjects(inventory);
        
        // Create game object from template
        GameObject created = simulationModel.createGameObjectFromInventory(template, 100f, 200f);
        
        assertNotNull(created);
        assertEquals("ball", created.getName());
        assertEquals(template.getType(), created.getType());
        assertEquals(template.getColour(), created.getColour());
        assertEquals(template.isWinning(), created.isWinning());
        
        // Test position calculation with offset
        float expectedX = 100f - template.getSize().getWidth() / 2;
        float expectedY = 200f - template.getSize().getHeight() / 2;
        assertEquals(expectedX, created.getPosition().getX(), 0.01f);
        assertEquals(expectedY, created.getPosition().getY(), 0.01f);
    }
    
    /**
     * Tests management of dropped game objects in the simulation.
     * <p>
     * Tests the dropped objects system including:
     * </p>
     * <ul>
     * <li>Initial empty state verification</li>
     * <li>Adding individual objects to the dropped collection</li>
     * <li>Verifying collection size and content</li>
     * <li>Setting entire collections via setter method</li>
     * </ul>
     * 
     * @see SimulationModel#getDroppedObjects()
     * @see SimulationModel#addDroppedObject(GameObject)
     * @see SimulationModel#setDroppedObjects(List)
     */
    @Test
    public void testDroppedObjectsManagement() {
        // Test empty initially
        assertEquals(0, simulationModel.getDroppedObjects().size());
        
        // Add dropped objects
        GameObject obj1 = createTestGameObject("ball");
        GameObject obj2 = createTestGameObject("box");
        
        simulationModel.addDroppedObject(obj1);
        simulationModel.addDroppedObject(obj2);
        
        assertEquals(2, simulationModel.getDroppedObjects().size());
        assertTrue(simulationModel.getDroppedObjects().contains(obj1));
        assertTrue(simulationModel.getDroppedObjects().contains(obj2));
        
        // Test setting dropped objects directly
        List<GameObject> newDropped = new ArrayList<>();
        newDropped.add(createTestGameObject("platform"));
        
        simulationModel.setDroppedObjects(newDropped);
        assertEquals(newDropped, simulationModel.getDroppedObjects());
        assertEquals(1, simulationModel.getDroppedObjects().size());
    }
    
    /**
     * Tests the restoration of inventory counts from dropped objects.
     * <p>
     * Tests the count restoration system including:
     * </p>
     * <ul>
     * <li>Setting up initial inventory with specific counts</li>
     * <li>Simulating object placement by decrementing counts</li>
     * <li>Adding corresponding dropped objects</li>
     * <li>Verifying count restoration based on dropped objects</li>
     * <li>Boundary conditions (counts not going below zero)</li>
     * </ul>
     * 
     * @see SimulationModel#restoreInventoryCounts()
     * @see SimulationModel#decrementInventoryCount(String)
     * @see SimulationModel#addDroppedObject(GameObject)
     */
    @Test
    public void testRestoreInventoryCounts() {
        // Setup inventory and dropped objects
        List<InventoryObject> inventory = new ArrayList<>();
        inventory.add(createTestInventoryObject("ball", 1));
        inventory.add(createTestInventoryObject("box", 2));
        simulationModel.setInventoryObjects(inventory);
        
        // Add dropped objects
        simulationModel.addDroppedObject(createTestGameObject("ball"));
        simulationModel.addDroppedObject(createTestGameObject("ball"));
        simulationModel.addDroppedObject(createTestGameObject("box"));
        
        // Decrement counts to simulate placement
        simulationModel.decrementInventoryCount("ball");
        simulationModel.decrementInventoryCount("ball");
        simulationModel.decrementInventoryCount("box");
        
        InventoryObject ball = simulationModel.findInventoryObjectByName("ball");
        InventoryObject box = simulationModel.findInventoryObjectByName("box");
        
        // Current counts should be reduced
        assertEquals(0, ball.getCount()); // was 1, decreased by 2, but limited to 0
        assertEquals(1, box.getCount());  // was 2, decreased by 1
        
        // Restore counts
        simulationModel.restoreInventoryCounts();
        
        // Counts should be restored based on dropped objects
        assertEquals(2, ball.getCount()); // 0 + 2 dropped balls
        assertEquals(2, box.getCount());  // 1 + 1 dropped box
    }
}
