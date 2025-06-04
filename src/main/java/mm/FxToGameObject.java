package mm;

import java.util.List;
import java.util.ArrayList;
import mm.model.objects.*;

import javafx.scene.shape.*;
import javafx.scene.paint.Color;

public class FxToGameObject {
    private static int nextname; 
    
    public static GameObject convertBack(PhysicsVisualPair pair) {
        GameObject gameObject = null;

        String name;
        Position position = new Position();
        Size size = new Size();
        String color;
        String type;
        Physics physics;

        Shape shape = pair.visual;
        //name = type + nextname;
        nextname++;

        Color tmp = (Color) shape.getFill();
        assert(tmp != null);

        if (shape instanceof Rectangle) {
        type = "Rectangle";
        javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) shape;
        float x = (float) rect.getTranslateX();
        float y = (float) rect.getTranslateY();
        float width = (float) rect.getWidth();
        float height = (float) rect.getHeight();

        position.setX(x);
        position.setY(y);

        size.setHeight(height);
        size.setWidth(width);

    } else if (shape instanceof Circle) {
        type = "Circle";
        javafx.scene.shape.Circle circle = (javafx.scene.shape.Circle) shape;
        float x = (float) circle.getTranslateX();
        float y = (float) circle.getTranslateY();
        float r = (float) circle.getRadius();

        position.setX(x);
        position.setY(y);
        //size = new Size(r * 2, r * 2); // Durchmesser als "size"
        //radius = r;
    } else {
        throw new IllegalArgumentException("Shape-Typ nicht unterstützt: " + shape.getClass());
    }


        //gameObject = new GameObject(name, type, position, size, colour, physics, radius)
        return gameObject;
    }
}