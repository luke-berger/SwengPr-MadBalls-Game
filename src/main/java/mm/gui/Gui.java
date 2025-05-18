package mm.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Gui extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MadBalls©");

        // Buttons
        Button btnPuzzle = new Button("Puzzle-Mode");
        Button btnSandbox = new Button("Sandbox-Mode");

        Button btnOptions = new Button("Options-Mode");

        // Placeholder actions
        btnPuzzle.setOnAction(e -> System.out.println("Starting Puzzle Mode"));
        btnSandbox.setOnAction(e -> System.out.println("Starting Sandbox Mode"));
        btnOptions.setOnAction(e -> System.out.println("Opening Options"));

        // Load and center background image
        Image backgroundImage = new Image(getClass().getResource("MB_TitleScreen.png").toExternalForm());
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setPreserveRatio(true);
        //
        //
        backgroundView.setFitWidth(1980); // optional: scales proportionally

        // StackPane for centered background
        StackPane backgroundLayer = new StackPane(backgroundView);
        backgroundLayer.setPrefSize(1980, 1080);

        // Pane for freely positioned buttons
        Pane buttonLayer = new Pane();

        // Set button positions and rotations (adjust as needed)
        btnPuzzle.setLayoutX(490);
        btnPuzzle.setLayoutY(470);
        btnPuzzle.setRotate(-10);

        btnSandbox.setLayoutX(780);
        btnSandbox.setLayoutY(470);
        btnSandbox.setRotate(7);

        btnOptions.setLayoutX(700);
        btnOptions.setLayoutY(600);
        btnOptions.setRotate(-5);

        // Assign CSS classes
        btnPuzzle.getStyleClass().add("btnPuzzle");
        btnSandbox.getStyleClass().add("btnPuzzle");
        btnOptions.getStyleClass().add("btnPuzzle");

        // Add buttons to button layer
        buttonLayer.getChildren().addAll(btnPuzzle, btnSandbox, btnOptions);

        // StackPane root: layers background and button pane
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundLayer, buttonLayer);

        // Create and style the scene
        Scene scene = new Scene(root, 1980, 1080);
        scene.getStylesheets().add(getClass().getResource("titleScreen.css").toExternalForm());

        // Show the stage
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}