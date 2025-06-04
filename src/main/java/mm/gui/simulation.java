package mm.gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mm.FxToGameObject;
import mm.GameObjectConverter;
import mm.ObjectImporter;
import mm.PhysicsVisualPair;
import mm.core.physics.ResettableAnimationTimer;
import mm.model.objects.GameObject;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.util.ArrayList;
import java.util.List;

public class simulation {

    private World world;
    private List<PhysicsVisualPair> pairs;
    private Pane simSpace;
    private ResettableAnimationTimer timer;
    private HBox bottomBar;

    public Scene getScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setId("root-pane");

        // Simulation area
        simSpace = new Pane();
        simSpace.getStyleClass().add("sim-space");
        root.setCenter(simSpace);

        // Sidebar
        VBox sideBar = new VBox();
        sideBar.getStyleClass().add("side-bar");
        sideBar.setPrefWidth(200);

        StackPane inventoryBox = new StackPane();
        inventoryBox.getStyleClass().add("inventory-box");
        VBox.setVgrow(inventoryBox, Priority.ALWAYS);

        HBox squareContainer = new HBox();
        squareContainer.getStyleClass().add("square-container");
        squareContainer.setAlignment(Pos.CENTER);

        StackPane menuSquare = new StackPane();
        menuSquare.getStyleClass().add("menu-square");

        // Grid with round buttons and icons
        GridPane grid = new GridPane();
        grid.getStyleClass().add("menu-grid");

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                Button btn = new Button();
                btn.getStyleClass().add("menu-button");

                FontIcon icon = null;

                if (row == 0 && col == 0) {
                    icon = new FontIcon(FontAwesomeSolid.PLAY); // Play
                    btn.setOnAction(e -> timer.start());
                } else if (row == 0 && col == 1) {
                    icon = new FontIcon(FontAwesomeSolid.STOP); // Stop
                    btn.setOnAction(e -> {
                        timer.stop();
                        setupSimulation();
                    });
                } else if (row == 0 && col == 2) {
                    icon = new FontIcon(FontAwesomeSolid.COGS); // Settings
                    btn.setOnAction(e -> {
                        TitleScreen titleScreen = new TitleScreen();
                        Scene titleScene = titleScreen.createTitleScene(primaryStage);
                        primaryStage.setScene(titleScene);
                    });
                } else if (row == 1 && col == 0) {
                    icon = new FontIcon(FontAwesomeSolid.FILE_EXPORT); // Export
                } else if (row == 1 && col == 1) {
                    icon = new FontIcon(FontAwesomeSolid.PAUSE); // Pause
                    btn.setOnAction(e -> timer.stop());
                } else if (row == 1 && col == 2) {
                    icon = new FontIcon(FontAwesomeSolid.SAVE); // Save
                    btn.setOnAction(e -> {
                        timer.stop();
                        setupSimulation();
                        exportLevel();
                    });
                }

                if (icon != null) {
                    icon.setIconSize(16);
                    icon.setIconColor(Color.WHITE);
                    btn.setGraphic(icon);
                }

                grid.add(btn, col, row);
            }
        }

        menuSquare.getChildren().add(grid);
        squareContainer.getChildren().add(menuSquare);
        sideBar.getChildren().addAll(inventoryBox, squareContainer);
        root.setRight(sideBar);

        // create bottom bar
        bottomBar = new HBox();
        bottomBar.getStyleClass().add("bottom-bar");
        bottomBar.setPrefHeight(150);
        root.setBottom(bottomBar);

        // initialize simulation
        setupSimulation();

        // bind root size to stage
        root.prefWidthProperty().bind(primaryStage.widthProperty());
        root.prefHeightProperty().bind(primaryStage.heightProperty());

        Scene scene = new Scene(root);

        // load CSS stylesheet
        scene.getStylesheets().add(
                getClass().getResource("/styling/simulation.css").toExternalForm());

        // force CSS and layout pass
        Platform.runLater(() -> {
            root.applyCss();
            root.layout();
        });

        return scene;
    }

    private void setupSimulation() {
        // clear all nodes except buttons
        simSpace.getChildren().removeIf(node -> !(node instanceof Button));

        // create new physics world
        world = new World(new Vec2(0.0f, 9.8f));
        pairs = new ArrayList<>();

        ObjectImporter importer = new ObjectImporter();
        List<GameObject> gameObjects = importer.getGameObjects();

        for (GameObject obj : gameObjects) {
            PhysicsVisualPair pair = GameObjectConverter.convert(obj, world);
            if (pair.visual != null) {
                simSpace.getChildren().add(pair.visual);
                pairs.add(pair);
            }
        }

        // create new timer for simulation
        timer = new ResettableAnimationTimer(world, pairs);
    }

    private void exportLevel() {
        // convert physics visuals back to game objects
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        for (PhysicsVisualPair pair : pairs) {
            GameObject obj = FxToGameObject.convertBack(pair);
            gameObjects.add(obj);
        }
        System.out.println("export done!");
    }
}
