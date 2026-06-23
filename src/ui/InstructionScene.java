package ui;

import game.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Instructions screen that lists controls, rules, and win conditions.
 */
public class InstructionScene {
    private final BorderPane view;

    /**
     * Creates the instructions screen.
     *
     * @param sceneManager scene navigation service
     */
    public InstructionScene(SceneManager sceneManager) {
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
     * Builds the instructions layout.
     *
     * @param sceneManager scene navigation service
     * @return configured root pane
     */
    private BorderPane createView(SceneManager sceneManager) {
        Label title = new Label("Instructions");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: 800; -fx-text-fill: #f2d16b;");

        Label text = new Label("""
                Player 1 Blue Tank
                W/S move, A/D rotate, SPACE shoots

                Player 2 Red Tank
                UP/DOWN move, LEFT/RIGHT rotate, ENTER shoots

                Each tank has 100 HP. Bullets deal 20 damage, bounce up to 5 times, and have a 500 ms cooldown.
                Destroy the opponent or have more HP when the 120 second timer expires.
                The winner of a round gains 1 point, both tanks respawn, and the map stays active.
                First player to 5 points wins the match.

                ESC opens the pause menu.
                """);
        text.setStyle("-fx-font-size: 18px; -fx-text-fill: #e8f0f2; -fx-line-spacing: 4;");
        text.setMaxWidth(720);

        Button back = new Button("Back");
        back.setPrefWidth(220);
        back.setPrefHeight(46);
        back.setStyle("-fx-font-size: 17px; -fx-font-weight: 700; -fx-background-radius: 6;"
                + "-fx-background-color: #e7edf0; -fx-text-fill: #172026;");
        back.setOnAction(event -> sceneManager.showMainMenu());

        VBox content = new VBox(24, title, text, back);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));

        BorderPane root = new BorderPane(content);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #182024, #2b3638);");
        return root;
    }
}
