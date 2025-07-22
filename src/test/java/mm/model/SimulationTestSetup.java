package mm.model;

import org.junit.jupiter.api.BeforeEach;

public class SimulationTestSetup {
    /** The SimulationModel instance under test. */
    protected SimulationModel simulationModel;
    
    /**
     * Sets up the test environment before each test method.
     * <p>
     * Initializes a fresh SimulationModel instance with a test level path.
     * This ensures each test starts with a clean state.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        simulationModel = new SimulationModel("/test/level.json");
    }
    
    /**
     * Creates a test InventoryObject with the specified name and count.
     * <p>
     * This helper method creates a fully configured InventoryObject with:
     * </p>
     * <ul>
     * <li>Standard test type and size (20x20)</li>
     * <li>Physics properties (density: 1.0, friction: 0.5, restitution: 0.3, shape: dynamic)</li>
     * <li>Default angle (0°) and color (BLACK)</li>
     * <li>Non-winning status</li>
     * </ul>
     * 
     * @param name the name of the inventory object
     * @param count the initial count of available items
     * @return a fully configured InventoryObject for testing
     */
    public InventoryObject createTestInventoryObject(String name, int count) {
        InventoryObject obj = new InventoryObject(name, "testType", new Size(20f, 20f));
        obj.setCount(count);
        obj.setPhysics(new Physics(1.0f, 0.5f, 0.3f, "dynamic"));
        obj.setAngle(0f);
        obj.setColour("BLACK");
        obj.setWinning(false);
        return obj;
    }
    
    /**
     * Creates a test GameObject with the specified name and default properties.
     * <p>
     * This helper method creates a fully configured GameObject with:
     * </p>
     * <ul>
     * <li>Standard test type and position (10, 20)</li>
     * <li>Standard test size (30x40)</li>
     * <li>Physics properties to avoid null pointer exceptions</li>
     * <li>Default color (BLACK)</li>
     * </ul>
     * 
     * @param name the name of the game object
     * @return a fully configured GameObject for testing
     */
    public GameObject createTestGameObject(String name) {
        GameObject obj = new GameObject(name, "testType", new Position(10f, 20f), new Size(30f, 40f));
        // Set physics to avoid null pointer exceptions in JSON generation
        obj.setPhysics(new Physics(1.0f, 0.4f, 0.1f, "dynamic"));
        obj.setColour("BLACK");
        return obj;
    }
}
