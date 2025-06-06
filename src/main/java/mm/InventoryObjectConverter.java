package mm;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import mm.model.objects.InventoryObject;
import mm.model.objects.Physics;


/*
 * Converts all objects under the inventory category from .json - File
 */
public class InventoryObjectConverter {
    private static final float SCALE = 50.0f;
    
    /**
     * Convert InventoryObjects to Objects in inventory during the game.
     * They should be limitless depending on the game mode. Shown in inventoryBox
     * @param obj The individual inventory object to be converted
     * @param world Environment where the bodys are created from 
     */
    public static PhysicsVisualPair convert(InventoryObject obj, World world){

        Physics physics = obj.getPhysics();
        String type = obj.getType(); // e.g., "rectangle", "circle"
        Shape visual = null;
        Body body = null;

        if ("rectangle".equalsIgnoreCase(type)){

            float width = obj.getSize().getWidth();
            float height = obj.getSize().getHeight();
            Rectangle rect = new Rectangle(width, height, Color.valueOf(obj.getColour()));
            visual = rect;

            BodyDef def = new BodyDef();
            def.type = physics.isDynamic() ? BodyType.DYNAMIC : BodyType.STATIC;
            body = world.createBody(def);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2 / SCALE, height / 2 / SCALE);

            FixtureDef fixture = new FixtureDef();
            fixture.shape = shape;
            fixture.density = physics.getDensity();
            fixture.friction = physics.getFriction();
            fixture.restitution = physics.getRestitution();
            body.createFixture(fixture);

        }else if ("circle".equalsIgnoreCase(type)){
            float radius = obj.getSize().getRadius();
            Circle circ = new Circle(radius, Color.valueOf(obj.getColour()));
            visual = circ;

            BodyDef def = new BodyDef();
            def.type = physics.isDynamic() ? BodyType.DYNAMIC : BodyType.STATIC;
            body = world.createBody(def);

            CircleShape shape = new CircleShape();
            shape.setRadius(radius / SCALE);

            FixtureDef fixture = new FixtureDef();
            fixture.shape = shape;
            fixture.density = physics.getDensity();
            fixture.friction = physics.getFriction();
            fixture.restitution = physics.getRestitution();
            body.createFixture(fixture);
        }

        return new PhysicsVisualPair(visual, body);
    }
}
