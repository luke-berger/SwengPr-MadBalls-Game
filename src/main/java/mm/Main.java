package mm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mm.gui.TitleScreen;
import mm.model.objects.Level;

public class Main extends Application {

    public static Level loadedLevel;

    @Override
    public void start(Stage primaryStage) {
        TitleScreen titleScreen = new TitleScreen();
        Scene titleScene = titleScreen.createTitleScene(primaryStage);
        primaryStage.setTitle("MadBalls©");
        primaryStage.setScene(titleScene);

        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    public static void main(String[] args) {

        System.out.println("Starting...");
        launch(args); // JavaFX starten
        System.out.println("Exiting...");
    }
}
