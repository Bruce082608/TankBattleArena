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
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: 900; -fx-text-fill: #1f2529;");

        Label text = new Label("""
                Player 1 Blue Tank
                E/D move forward and backward, S/F rotate, Q shoots

                Player 2 Red Tank
                UP/DOWN move forward and backward, LEFT/RIGHT rotate, M shoots

                Player 3 Green Tank
                Move the mouse to drive and aim, left click shoots

                Each tank has 3 HP. Regular bullets and mines deal 1 damage.
                Regular bullets bounce around the arena and stay dangerous for several seconds.
                Laser pickup shows a reflected aim preview, then fires a beam that deals 3 damage.
                The last tank alive gains 1 point, then everyone respawns.
                First player to 5 points wins the match.

                Pickups: Gatling, Mine, Laser, Shotgun, Shield, and Homing Missile.
                ESC opens the pause menu.
                """);
        text.setStyle("-fx-font-size: 18px; -fx-text-fill: #20262b; -fx-line-spacing: 4;");
        text.setMaxWidth(720);

        Button back = new Button("Back");
        back.setPrefWidth(220);
        back.setPrefHeight(46);
        back.setStyle("-fx-font-size: 17px; -fx-font-weight: 700; -fx-background-radius: 6;"
                + "-fx-background-color: #20262b; -fx-text-fill: #f5f2e9;");
        back.setOnAction(event -> sceneManager.showMainMenu());

        VBox content = new VBox(24, title, text, back);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));

        BorderPane root = new BorderPane(content);
        root.setStyle("-fx-background-color: #f5f2e9;");
        return root;
    }
}
