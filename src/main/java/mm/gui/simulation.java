package mm.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mm.GameObjectConverter;
import mm.ObjectImporter;
import mm.PhysicsVisualPair;
import mm.model.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class simulation extends Application {

    World world = new World(new Vec2(0.0f, 9.8f));

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        
        ObjectImporter importer = new ObjectImporter();
        List<GameObject> gameObjects = importer.getGameObjects();
        List<PhysicsVisualPair> pairs = new ArrayList<PhysicsVisualPair>();
        
        for (GameObject obj: gameObjects) {
            PhysicsVisualPair pair = GameObjectConverter.convert(obj, world);
            if (pair.visual != null){
                root.getChildren().add(pair.visual);
                pairs.add(pair);
            }
        }

        

        // Animation loop
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                float timeStep = (now - lastTime) / 1_000_000_000.0f;
                world.step(timeStep, 8, 3);
                lastTime = now;

                // Synchronize JavaFX shapes with JBox2D bodies
                for (PhysicsVisualPair pair : pairs) {
                    if (pair.visual != null && pair.body != null) {
                        // SCALE must match the one used in GameObjectConverter
                        float SCALE = 50.0f;
                        Vec2 pos = pair.body.getPosition();
                        double angle = Math.toDegrees(pair.body.getAngle());

                        // For rectangles, set center
                        if (pair.visual instanceof javafx.scene.shape.Rectangle) {
                            javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) pair.visual;
                            rect.setTranslateX(pos.x * SCALE - rect.getWidth() / 2);
                            rect.setTranslateY(pos.y * SCALE - rect.getHeight() / 2);
                            rect.setRotate(angle);
                        }
                        // For circles, set center
                        else if (pair.visual instanceof javafx.scene.shape.Circle) {
                            javafx.scene.shape.Circle circ = (javafx.scene.shape.Circle) pair.visual;
                            circ.setTranslateX(pos.x * SCALE);
                            circ.setTranslateY(pos.y * SCALE);
                            circ.setRotate(angle);
                        }
                    }
                }
            }
        };

        Button btnStart = new Button("Start Simulation");
        btnStart.setTranslateX(10);
        btnStart.setTranslateY(10);
        btnStart.setOnAction(e -> timer.start());
        root.getChildren().add(btnStart);

        Button btnStop = new Button("Stop Simulation");
        btnStop.setTranslateX(120);
        btnStop.setTranslateY(10);
        btnStop.setOnAction(e -> timer.stop());
        root.getChildren().add(btnStop);

        primaryStage.setTitle("JavaFX + JBox2D Physics Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
