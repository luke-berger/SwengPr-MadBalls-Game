package mm.model;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test-Klasse für die Level-Klasse.
 * Testet die Funktionalität des Level-Objekts inklusive der Verwaltung von
 * GameObjects und InventoryObjects.
 * 
 * @version 1.0
 */
public class TestLevel {
    
    /**
     * Testet die grundlegende Funktionalität der Level-Klasse.
     * Überprüft die Objekterstellung und die Setter/Getter-Methoden für
     * LevelObjects und InventoryObjects.
     */
    @Test
    public void testLevel() {
        Level testLevel = new Level();
        assertNotEquals(null, testLevel);
        assertNotEquals(null, testLevel.getClass());

        List<GameObject> gameObjects = new ArrayList<>();
        List<InventoryObject> inventoryObjects = new ArrayList<>();

        testLevel.setLevelObjects(gameObjects);
        testLevel.setInventoryObjects(inventoryObjects);
        assertEquals(gameObjects, testLevel.getLevelObjects());
        assertEquals(inventoryObjects, testLevel.getInventoryObjects());
    }
}