package mm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TestSize {
    @Test
    public void testSize() {
        Size testSize = new Size();
        assertNotEquals(null, testSize);
        assertNotEquals(null, testSize.getClass());        
    }

     @Test
    public void testWidthGetterAndSetter() {
        Size size = new Size();
        float expectedWidth = 123.45f;
        size.setWidth(expectedWidth);
        assertEquals(expectedWidth, size.getWidth(), 0.0001f);
    }

    @Test
    public void testHeightGetterAndSetter() {
        Size size = new Size();
        float expectedHeight = 67.89f;
        size.setHeight(expectedHeight);
        assertEquals(expectedHeight, size.getHeight(), 0.0001f);
    }

    @Test
    public void testRadiusGetterAndSetter() {
        Size size = new Size();
        float expectedRadius = 42.0f;
        size.setRadius(expectedRadius);
        assertEquals(expectedRadius, size.getRadius(), 0.0001f);
    }
}
