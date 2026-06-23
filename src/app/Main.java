package app;

import game.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Starts the Tank Battle Arena desktop game.
 */
public class Main extends Application {
    /** Width of the application window. */
    public static final int WINDOW_WIDTH = 1100;

    /** Height of the application window. */
    public static final int WINDOW_HEIGHT = 760;

    /**
     * Creates the primary stage and displays the main menu.
     *
     * @param stage JavaFX primary stage supplied by the runtime
     */
    @Override
    public void start(Stage stage) {
        SceneManager sceneManager = new SceneManager(stage);
        stage.setTitle("Tank Battle Arena");
        stage.setResizable(false);
        sceneManager.showMainMenu();
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }
}
