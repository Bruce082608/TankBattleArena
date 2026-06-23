package ui;

import game.SceneManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Main menu screen for starting and configuring the game.
 */
public class MainMenu {
    private final BorderPane view;

    /**
     * Creates the main menu.
     *
     * @param sceneManager scene navigation service
     */
    public MainMenu(SceneManager sceneManager) {
        this.view = createView(sceneManager);
    }

    /**
     * Gets the menu root node.
     *
     * @return JavaFX parent node
     */
    public Parent getView() {
        return view;
    }

    /**
     * Builds the main menu layout.
     *
     * @param sceneManager scene navigation service
     * @return configured menu pane
     */
    private BorderPane createView(SceneManager sceneManager) {
        Label title = new Label("Tank Battle Arena");
        title.setStyle("-fx-font-size: 52px; -fx-font-weight: 800; -fx-text-fill: #f2d16b;");

        Label selectedMap = new Label("Selected Map: " + sceneManager.getSelectedMap().getName());
        selectedMap.setStyle("-fx-font-size: 18px; -fx-text-fill: #d8e6ea;");

        Button start = createMenuButton("Start Game");
        start.setOnAction(event -> sceneManager.startGame());

        Button selectMap = createMenuButton("Select Map");
        selectMap.setOnAction(event -> sceneManager.showMapSelection());

        Button instructions = createMenuButton("Instructions");
        instructions.setOnAction(event -> sceneManager.showInstructions());

        Button exit = createMenuButton("Exit");
        exit.setOnAction(event -> Platform.exit());

        VBox menu = new VBox(18, title, selectedMap, start, selectMap, instructions, exit);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(60));

        BorderPane root = new BorderPane(menu);
        root.setStyle("-fx-background-color: radial-gradient(center 50% 35%, radius 75%, #32444a, #151c20);");
        return root;
    }

    /**
     * Creates a consistently styled main menu button.
     *
     * @param text button label
     * @return configured button
     */
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(260);
        button.setPrefHeight(48);
        button.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-background-radius: 6;"
                + "-fx-background-color: #e7edf0; -fx-text-fill: #182126;");
        return button;
    }
}
