package ui;

import entities.PlayerTank;
import entities.Tank;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import systems.ScoreManager;
import systems.TimerManager;

/**
 * Heads-up display for scores, timer, health bars, and round messages.
 */
public class GameHUD extends BorderPane {
    private final Label playerOneScore = new Label();
    private final Label playerTwoScore = new Label();
    private final Label timerLabel = new Label();
    private final Label roundMessage = new Label();
    private final ProgressBar playerOneHp = new ProgressBar(1);
    private final ProgressBar playerTwoHp = new ProgressBar(1);

    /**
     * Creates the HUD for a map.
     *
     * @param mapName name of the active map
     */
    public GameHUD(String mapName) {
        setPadding(new Insets(10, 30, 8, 30));
        setStyle("-fx-background-color: #11191d; -fx-border-color: #31444c; -fx-border-width: 0 0 2 0;");
        setLeft(createPlayerPanel("Player 1", playerOneScore, playerOneHp, "#46a7ff"));
        setCenter(createCenterPanel(mapName));
        setRight(createPlayerPanel("Player 2", playerTwoScore, playerTwoHp, "#ff5d67"));
    }

    /**
     * Updates HUD labels and HP bars from current game state.
     *
     * @param scoreManager current score state
     * @param timerManager current timer state
     * @param playerOne player one tank
     * @param playerTwo player two tank
     */
    public void update(
            ScoreManager scoreManager,
            TimerManager timerManager,
            PlayerTank playerOne,
            PlayerTank playerTwo) {
        playerOneScore.setText("Score: " + scoreManager.getPlayerOneScore());
        playerTwoScore.setText("Score: " + scoreManager.getPlayerTwoScore());
        timerLabel.setText(timerManager.formatTime());
        playerOneHp.setProgress(playerOne.getHp() / (double) Tank.MAX_HP);
        playerTwoHp.setProgress(playerTwo.getHp() / (double) Tank.MAX_HP);
    }

    /**
     * Shows a temporary round result message.
     *
     * @param message result message
     */
    public void showRoundMessage(String message) {
        roundMessage.setText(message);
    }

    /**
     * Clears the round result message.
     */
    public void clearRoundMessage() {
        roundMessage.setText("");
    }

    /**
     * Creates a player score and HP panel.
     *
     * @param name player label
     * @param scoreLabel score label to place
     * @param hpBar health bar to place
     * @param accentColor CSS color for the health bar
     * @return configured panel
     */
    private VBox createPlayerPanel(String name, Label scoreLabel, ProgressBar hpBar, String accentColor) {
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: " + accentColor + ";");
        scoreLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #edf6f8;");
        hpBar.setPrefWidth(190);
        hpBar.setStyle("-fx-accent: " + accentColor + ";");
        VBox box = new VBox(4, nameLabel, scoreLabel, hpBar);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    /**
     * Creates the centered timer and message panel.
     *
     * @param mapName active map name
     * @return configured center panel
     */
    private VBox createCenterPanel(String mapName) {
        Label mapLabel = new Label(mapName);
        mapLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #9fb2b8;");
        timerLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: 900; -fx-text-fill: #f2d16b;");
        roundMessage.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #e9f4f6;");
        HBox timerBox = new HBox(timerLabel);
        timerBox.setAlignment(Pos.CENTER);
        VBox box = new VBox(2, mapLabel, timerBox, roundMessage);
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
