package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test-Klasse für die Size-Klasse.
 * Testet die Funktionalität der Size-Klasse inklusive aller Dimensionseigenschaften
 * wie Width, Height und Radius.
 * 
 * @version 1.0
 */
public class TestSize {
    
    /**
     * Testet die grundlegende Funktionalität der Size-Klasse.
     * Überprüft die Objekterstellung und stellt sicher, dass das Objekt nicht null ist.
     */
    @Test
    public void testSize() {
        Size testSize = new Size();
        assertNotEquals(null, testSize);
        assertNotEquals(null, testSize.getClass());        
    }

    /**
     * Testet die Width-Setter- und Getter-Methoden der Size-Klasse.
     * Überprüft, ob die gesetzte Breite korrekt zurückgegeben wird.
     */
    @Test
    public void testWidthGetterAndSetter() {
        Size size = new Size();
        float expectedWidth = 123.45f;
        size.setWidth(expectedWidth);
        assertEquals(expectedWidth, size.getWidth(), 0.0001f);
    }

    /**
     * Testet die Height-Setter- und Getter-Methoden der Size-Klasse.
     * Überprüft, ob die gesetzte Höhe korrekt zurückgegeben wird.
     */
    @Test
    public void testHeightGetterAndSetter() {
        Size size = new Size();
        float expectedHeight = 67.89f;
        size.setHeight(expectedHeight);
        assertEquals(expectedHeight, size.getHeight(), 0.0001f);
    }

    /**
     * Testet die Radius-Setter- und Getter-Methoden der Size-Klasse.
     * Überprüft, ob der gesetzte Radius korrekt zurückgegeben wird.
     */
    @Test
    public void testRadiusGetterAndSetter() {
        Size size = new Size();
        float expectedRadius = 42.0f;
        size.setRadius(expectedRadius);
        assertEquals(expectedRadius, size.getRadius(), 0.0001f);
    }
}