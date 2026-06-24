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
import systems.ScoreManager;

/**
 * Game-over scene shown after a player reaches the match score target.
 */
public class GameOverScene {
    private final BorderPane view;

    /**
     * Creates the game-over scene.
     *
     * @param sceneManager scene navigation service
     * @param mapData map used for the match
     * @param scoreManager final score state
     */
    public GameOverScene(SceneManager sceneManager, MapData mapData, ScoreManager scoreManager) {
        this.view = createView(sceneManager, mapData, scoreManager);
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
     * Builds the game-over layout.
     *
     * @param sceneManager scene navigation service
     * @param mapData map used for the match
     * @param scoreManager final score state
     * @return configured root pane
     */
    private BorderPane createView(SceneManager sceneManager, MapData mapData, ScoreManager scoreManager) {
        Label title = new Label("Game Over");
        title.setStyle("-fx-font-size: 50px; -fx-font-weight: 900; -fx-text-fill: #1f2529;");

        Label winner = new Label(scoreManager.getWinnerName() + " wins");
        winner.setStyle("-fx-font-size: 30px; -fx-font-weight: 800; -fx-text-fill: #20262b;");

        Label summary = new Label("Final Score  "
                + scoreManager.formatScores()
                + "\nMap: "
                + mapData.getName());
        summary.setStyle("-fx-font-size: 18px; -fx-text-fill: #354148; -fx-text-alignment: center;");

        Button playAgain = createButton("Play Again");
        playAgain.setOnAction(event -> sceneManager.restartMatch());

        Button mainMenu = createButton("Main Menu");
        mainMenu.setOnAction(event -> sceneManager.showMainMenu());

        VBox content = new VBox(20, title, winner, summary, playAgain, mainMenu);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));

        BorderPane root = new BorderPane(content);
        root.setStyle("-fx-background-color: #f5f2e9;");
        return root;
    }

    /**
     * Creates a consistently styled game-over button.
     *
     * @param text button label
     * @return configured button
     */
    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setPrefHeight(46);
        button.setStyle("-fx-font-size: 17px; -fx-font-weight: 800; -fx-background-radius: 6;"
                + "-fx-background-color: #20262b; -fx-text-fill: #f5f2e9;");
        return button;
    }
}
