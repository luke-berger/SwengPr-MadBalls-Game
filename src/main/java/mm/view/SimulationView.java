package mm.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/**
 * The {@code SimulationView} class is responsible for constructing and managing
 * the JavaFX UI components for the simulation screen in the MadBalls game.
 * <p>
 * This class contains only UI-related code and exposes methods to access
 * and update the main panes and controls. All simulation logic and state
 * should be handled by the controller and model, following the MVC pattern.
 * </p>
public class SimulationView {

    // Style constants to avoid duplicate literals
    private static final String CIRCLE_BUTTON_CLASS = "circle-button";
    private static final String WHITE_LABEL_STYLE = "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;";
    private static final String MENU_BUTTON_CLASS = "menu-button";
    private static final String OVERLAY_BACKGROUND = "rgba(0, 0, 0, 0.6)";
    
    // Core layout components
    private BorderPane mainPane;
    private Pane simSpace;
    private HBox bottomBar;
    private VBox sideBar;
    private StackPane overlaySettings;
    private StackPane winScreenOverlay;
    private Scene scene;
    private StackPane rootStack;
    
    // Inventory components
    private StackPane inventoryBox;
    private VBox inventoryItemBox;

    // Control buttons - grouped by functionality to reduce field count
    private final SimulationButtons simulationButtons = new SimulationButtons();
    private final OverlayButtons overlayButtons = new OverlayButtons();
    private final WinScreenButtons winScreenButtons = new WinScreenButtons();

    /**
     * Inner class to hold simulation control buttons, reducing field count.
     */
    public static class SimulationButtons {
        public Button playButton;
        public Button stopButton;
        public Button settingsButton;
        public Button deleteButton;
        public Button importButton;
        public Button saveButton;
        public Button crownButton;
    }

    /**
     * Inner class to hold overlay menu buttons, reducing field count.
     */
    public static class OverlayButtons {
        public Button overlayBackButton;
        public Button overlayQuitButton;
        public Button overlayCloseButton;
    }

    /**
     * Inner class to hold win screen buttons, reducing field count.
     */
    public static class WinScreenButtons {
        public Button btnWinHome;
        public Button btnWinNext;
        public Button btnWinExport;
    }

    /**
     * Constructs the SimulationView and builds the UI layout for the simulation screen.
     * Reduced cyclomatic complexity by breaking down into smaller methods.
     */
    public SimulationView(Stage primaryStage, boolean isPuzzleMode, boolean atPuzzlesEnd) {
        initializeMainComponents();
        createSideBarWithMenuButtons(isPuzzleMode);
        setupMainLayout();
        createOverlays(primaryStage, isPuzzleMode, atPuzzlesEnd);
        setupRootStackAndScene(primaryStage);
    }

    /**
     * Initializes the main UI components.
     */
    private void initializeMainComponents() {
        mainPane = new BorderPane();
        mainPane.setId("root-pane");

        simSpace = new Pane();
        simSpace.getStyleClass().add("sim-space");

        bottomBar = new HBox();
        bottomBar.getStyleClass().add("bottom-bar");
        bottomBar.setPrefHeight(150);
    }

    /**
     * Creates the sidebar with inventory and menu buttons.
     */
    private void createSideBarWithMenuButtons(boolean isPuzzleMode) {
        sideBar = new VBox();
        sideBar.getStyleClass().add("side-bar");
        sideBar.setPrefWidth(200);

        createInventoryComponents();
        createMenuGrid(isPuzzleMode);
    }

    /**
     * Creates the inventory box and item container.
     */
    private void createInventoryComponents() {
        inventoryBox = new StackPane();
        inventoryBox.getStyleClass().add("inventory-box");
        VBox.setVgrow(inventoryBox, Priority.ALWAYS);
        inventoryItemBox = new VBox();
        inventoryBox.getChildren().add(inventoryItemBox);
        inventoryItemBox.getStyleClass().add("inventoryItemBox");
    }

    /**
     * Creates the menu grid with control buttons.
     */
    private void createMenuGrid(boolean isPuzzleMode) {
        HBox squareContainer = new HBox();
        squareContainer.getStyleClass().add("square-container");
        squareContainer.setAlignment(Pos.CENTER);

        StackPane menuSquare = new StackPane();
        menuSquare.getStyleClass().add("menu-square");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("menu-grid");

        populateMenuGrid(grid, isPuzzleMode);

        menuSquare.getChildren().add(grid);
        squareContainer.getChildren().add(menuSquare);
        sideBar.getChildren().addAll(inventoryBox, squareContainer);
    }

    /**
     * Populates the menu grid with buttons and icons.
     */
    private void populateMenuGrid(GridPane grid, boolean isPuzzleMode) {
        String resetIcon = isPuzzleMode ? "REDO_ALT" : "TRASH_ALT";

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button btn = createMenuButton();
                FontIcon icon = createIconForPosition(row, col, resetIcon, isPuzzleMode, btn);
                
                if (icon != null) {
                    setupIcon(icon);
                    btn.setGraphic(icon);
                }

                grid.add(btn, col, row);
            }
        }
    }

    /**
     * Creates a standard menu button.
     */
    private Button createMenuButton() {
        Button btn = new Button();
        btn.getStyleClass().add(MENU_BUTTON_CLASS);
        return btn;
    }

    /**
     * Creates the appropriate icon for a grid position and assigns the button reference.
     */
    private FontIcon createIconForPosition(int row, int col, String resetIcon, boolean isPuzzleMode, Button btn) {
        if (row == 0 && col == 0) {
            simulationButtons.playButton = btn;
            return new FontIcon(FontAwesomeSolid.PLAY);
        } else if (row == 0 && col == 1) {
            simulationButtons.stopButton = btn;
            return new FontIcon(FontAwesomeSolid.STOP);
        } else if (row == 0 && col == 2) {
            simulationButtons.settingsButton = btn;
            return new FontIcon(FontAwesomeSolid.COGS);
        } else if (row == 1 && col == 0) {
            simulationButtons.deleteButton = btn;
            return new FontIcon(FontAwesomeSolid.valueOf(resetIcon));
        } else if (row == 1 && col == 1 && !isPuzzleMode) {
            simulationButtons.importButton = btn;
            return new FontIcon(FontAwesomeSolid.FOLDER_PLUS);
        } else if (row == 1 && col == 2) {
            simulationButtons.saveButton = btn;
            return new FontIcon(FontAwesomeSolid.SAVE);
        } else if (row == 2 && col == 0) {
            simulationButtons.crownButton = btn;
            return new FontIcon(FontAwesomeSolid.CROWN);
        }
        return null;
    }

    /**
     * Configures the standard properties for menu icons.
     */
    private void setupIcon(FontIcon icon) {
        icon.setIconSize(16);
        icon.setIconColor(Color.WHITE);
    }

    /**
     * Sets up the main layout by arranging components.
     */
    private void setupMainLayout() {
        mainPane.setCenter(simSpace);
        mainPane.setRight(sideBar);
        mainPane.setBottom(bottomBar);
    }

    /**
     * Creates both overlay components.
     */
    private void createOverlays(Stage primaryStage, boolean isPuzzleMode, boolean atPuzzlesEnd) {
        overlaySettings = createQuickMenuOverlay();
        overlaySettings.setVisible(false);

        winScreenOverlay = createWinScreenOverlay(primaryStage, isPuzzleMode, atPuzzlesEnd);
        winScreenOverlay.setVisible(false);
    }

    /**
     * Sets up the root stack pane and scene.
     */
    private void setupRootStackAndScene(Stage primaryStage) {
        rootStack = new StackPane();
        rootStack.getChildren().addAll(mainPane, overlaySettings, winScreenOverlay);
        rootStack.prefWidthProperty().bind(primaryStage.widthProperty());
        rootStack.prefHeightProperty().bind(primaryStage.heightProperty());

        scene = new Scene(rootStack);
        scene.getStylesheets().add(
                getClass().getResource("/styling/simulation.css").toExternalForm());
    }

    /**
     * Creates the quick menu overlay for settings, returning to the title screen,
     * and quitting the game. Reduced NCSS count by breaking into smaller methods.
     */
    private StackPane createQuickMenuOverlay() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(30, 30, 50, 0.7);");
        overlay.setPickOnBounds(true);

        VBox window = createOverlayWindow();
        setupOverlayButtons(window);

        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    /**
     * Creates the overlay window container.
     */
    private VBox createOverlayWindow() {
        VBox window = new VBox(20);
        window.setAlignment(Pos.TOP_CENTER);
        window.setPadding(new Insets(15));
        window.setMaxWidth(300);
        window.setMaxHeight(180);
        window.setBackground(new Background(new BackgroundFill(
                Color.rgb(10, 10, 20, 0.9), new CornerRadii(10), Insets.EMPTY)));
        return window;
    }

    /**
     * Sets up the overlay buttons and adds them to the window.
     */
    private void setupOverlayButtons(VBox window) {
        HBox topRow = new HBox();
        overlayButtons.overlayCloseButton = new Button("✕");
        overlayButtons.overlayCloseButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topRow.getChildren().addAll(spacer, overlayButtons.overlayCloseButton);

        overlayButtons.overlayBackButton = new Button("Back to Title Screen");
        overlayButtons.overlayBackButton.getStyleClass().add(MENU_BUTTON_CLASS);
        overlayButtons.overlayBackButton.setMaxWidth(Double.MAX_VALUE);
        overlayButtons.overlayBackButton.setPrefHeight(40);

        overlayButtons.overlayQuitButton = new Button("Quit Game");
        overlayButtons.overlayQuitButton.getStyleClass().add(MENU_BUTTON_CLASS);
        overlayButtons.overlayQuitButton.setMaxWidth(Double.MAX_VALUE);
        overlayButtons.overlayQuitButton.setPrefHeight(40);

        window.getChildren().addAll(topRow, overlayButtons.overlayBackButton, overlayButtons.overlayQuitButton);
    }

    /**
     * Creates a win‑screen overlay with a crown icon and optional Next Level button.
     * Reduced NCSS count by breaking into smaller methods.
     */
    private StackPane createWinScreenOverlay(Stage ownerStage, boolean isPuzzleMode, boolean atPuzzlesEnd) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: " + OVERLAY_BACKGROUND + ";");
        overlay.setVisible(false);
        overlay.prefWidthProperty().bind(ownerStage.widthProperty());
        overlay.prefHeightProperty().bind(ownerStage.heightProperty());

        VBox window = createWinWindow(atPuzzlesEnd);
        HBox buttonRow = createWinButtonRow(isPuzzleMode, atPuzzlesEnd, overlay);

        window.getChildren().add(buttonRow);
        overlay.getChildren().add(window);
        StackPane.setAlignment(window, Pos.CENTER);

        return overlay;
    }

    /**
     * Creates the win screen window with crown and title.
     */
    private VBox createWinWindow(boolean atPuzzlesEnd) {
        VBox window = new VBox(20);
        window.setAlignment(Pos.TOP_CENTER);
        window.setPadding(new Insets(30));
        window.setMaxWidth(600);
        window.setMaxHeight(250);
        window.setBackground(new Background(new BackgroundFill(
                Color.rgb(20, 20, 40, 0.9),
                new CornerRadii(16), Insets.EMPTY)));
        window.getStyleClass().add("win-window");

        FontIcon crown = new FontIcon(FontAwesomeSolid.CROWN);
        crown.setIconSize(48);
        crown.setIconColor(Color.GOLD);

        String titleText = atPuzzlesEnd ? "Puzzle Series Complete!" : "Level Complete!";
        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        window.getChildren().addAll(crown, title);
        return window;
    }

    /**
     * Creates the button row for the win screen based on mode and puzzle status.
     */
    private HBox createWinButtonRow(boolean isPuzzleMode, boolean atPuzzlesEnd, StackPane overlay) {
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setPadding(new Insets(20, 0, 0, 0));

        HBox mainMenuBox = createWinButton(FontAwesomeSolid.HOME, "Main Menu", overlay);
        HBox exportBox = createWinButton(FontAwesomeSolid.FILE_EXPORT, "Export Level", null);

        if (isPuzzleMode && !atPuzzlesEnd) {
            HBox nextBox = createWinButton(FontAwesomeSolid.ARROW_RIGHT, "Next Level", overlay);
            addButtonsWithSpacing(buttonRow, mainMenuBox, exportBox, nextBox);
        } else if (isPuzzleMode && atPuzzlesEnd) {
            addButtonsWithSpacing(buttonRow, mainMenuBox, exportBox);
        } else {
            HBox resumeBox = createWinButton(FontAwesomeSolid.PENCIL_ALT, "Resume Editing", overlay);
            addButtonsWithSpacing(buttonRow, mainMenuBox, resumeBox);
        }

        return buttonRow;
    }

    /**
     * Creates a win screen button with icon and label.
     */
    private HBox createWinButton(FontAwesomeSolid iconType, String labelText, StackPane overlay) {
        Button btn = new Button();
        btn.getStyleClass().add(CIRCLE_BUTTON_CLASS);
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(20);
        icon.setIconColor(Color.WHITE);
        btn.setGraphic(icon);

        if (overlay != null) {
            btn.setOnAction(e -> overlay.setVisible(false));
        }

        // Store button references
        if ("Main Menu".equals(labelText)) {
            winScreenButtons.btnWinHome = btn;
        } else if ("Next Level".equals(labelText)) {
            winScreenButtons.btnWinNext = btn;
        } else if ("Export Level".equals(labelText)) {
            winScreenButtons.btnWinExport = btn;
        }

        Label label = new Label(labelText);
        label.setStyle(WHITE_LABEL_STYLE);
        
        HBox buttonBox = new HBox(8, btn, label);
        if ("Next Level".equals(labelText)) {
            buttonBox = new HBox(8, label, btn); // Reverse order for next button
        }
        buttonBox.setAlignment(Pos.CENTER);

        return buttonBox;
    }

    /**
     * Adds buttons to the button row with proper spacing.
     */
    private void addButtonsWithSpacing(HBox buttonRow, HBox... buttons) {
        for (int i = 0; i < buttons.length; i++) {
            buttonRow.getChildren().add(buttons[i]);
            if (i < buttons.length - 1) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                buttonRow.getChildren().add(spacer);
            }
        }
    }

    // Getter methods for accessing components
    public Scene getScene() { return scene; }
    public Pane getSimSpace() { return simSpace; }
    public StackPane getInventoryBox() { return inventoryBox; }
    public VBox getInventoryItemBox() { return inventoryItemBox; }
    public VBox getSideBar() { return sideBar; }
    public HBox getBottomBar() { return bottomBar; }
    public StackPane getOverlaySettings() { return overlaySettings; }
    public StackPane getWinScreenOverlay() { return winScreenOverlay; }
    public StackPane getRootStack() { return rootStack; }

    // Button accessor methods
    public SimulationButtons getSimulationButtons() { return simulationButtons; }
    public OverlayButtons getOverlayButtons() { return overlayButtons; }
    public WinScreenButtons getWinScreenButtons() { return winScreenButtons; }
}
