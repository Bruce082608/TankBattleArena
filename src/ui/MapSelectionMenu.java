package ui;

import game.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import maps.MapData;

/**
 * Screen for choosing one of the three playable maps.
 */
public class MapSelectionMenu {
    private final BorderPane view;

    /**
     * Creates the map selection screen.
     *
     * @param sceneManager scene navigation service
     */
    public MapSelectionMenu(SceneManager sceneManager) {
        this.view = createView(sceneManager);
    }

    /**
     * Gets the screen root node.
     *
     * @return JavaFX parent node
     */
    public Parent getView() {
        return view;
    }

    /**
     * Builds the map selection layout.
     *
     * @param sceneManager scene navigation service
     * @return configured root pane
     */
    private BorderPane createView(SceneManager sceneManager) {
        Label title = new Label("Select Map");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: 800; -fx-text-fill: #f2d16b;");

        VBox content = new VBox(16, title);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));

        int mapNumber = 1;
        for (MapData map : sceneManager.getMaps()) {
            Button button = createMapButton("Map " + mapNumber + ": " + map.getName(), map.getDescription());
            button.setOnAction(event -> {
                sceneManager.selectMap(map);
                sceneManager.showMainMenu();
            });
            content.getChildren().add(button);
            mapNumber++;
        }

        Button back = createSimpleButton("Back");
        back.setOnAction(event -> sceneManager.showMainMenu());
        content.getChildren().add(back);

        BorderPane root = new BorderPane(content);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #192327, #28363b);");
        return root;
    }

    /**
     * Creates a map button with a compact description.
     *
     * @param title map title
     * @param description map description
     * @return configured button
     */
    private Button createMapButton(String title, String description) {
        Button button = createSimpleButton(title + "\n" + description);
        button.setPrefHeight(74);
        return button;
    }

    /**
     * Creates a consistently styled button.
     *
     * @param text button text
     * @return configured button
     */
    private Button createSimpleButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(430);
        button.setPrefHeight(46);
        button.setWrapText(true);
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-background-radius: 6;"
                + "-fx-background-color: #e7edf0; -fx-text-fill: #172026;");
        return button;
    }
}
