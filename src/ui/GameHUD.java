package ui;

import entities.PlayerTank;
import entities.Tank;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import systems.ScoreManager;

/**
 * Heads-up display for scores, pickups, and round messages.
 */
public class GameHUD extends BorderPane {
    private final Label playerOneScore = new Label();
    private final Label playerTwoScore = new Label();
    private final Label playerThreeScore = new Label();
    private final Label targetLabel = new Label();
    private final Label roundMessage = new Label();
    private final Label playerOneWeapon = new Label();
    private final Label playerTwoWeapon = new Label();
    private final Label playerThreeWeapon = new Label();
    private final VBox playerThreePanel;

    /**
     * Creates the HUD for a map.
     *
     * @param mapName name of the active map
     */
    public GameHUD(String mapName) {
        setPadding(new Insets(10, 30, 8, 30));
        setStyle("-fx-background-color: #f3f0e7; -fx-border-color: #22272b; -fx-border-width: 0 0 3 0;");
        setLeft(createPlayerPanel("Player 1", playerOneScore, playerOneWeapon, "#2868d8"));
        setCenter(createCenterPanel(mapName));
        VBox playerTwoPanel = createPlayerPanel("Player 2", playerTwoScore, playerTwoWeapon, "#cf2f35");
        playerThreePanel = createPlayerPanel("Player 3", playerThreeScore, playerThreeWeapon, "#33914a");
        HBox right = new HBox(26, playerTwoPanel, playerThreePanel);
        right.setAlignment(Pos.CENTER_RIGHT);
        setRight(right);
        playerThreePanel.setVisible(false);
        playerThreePanel.setManaged(false);
    }

    /**
     * Updates HUD labels from current game state.
     *
     * @param scoreManager current score state
     * @param playerOne player one tank
     * @param playerTwo player two tank
     * @param playerThree optional player three tank
     */
    public void update(
            ScoreManager scoreManager,
            PlayerTank playerOne,
            PlayerTank playerTwo,
            PlayerTank playerThree) {
        playerOneScore.setText("Score " + scoreManager.getPlayerOneScore());
        playerTwoScore.setText("Score " + scoreManager.getPlayerTwoScore());
        playerOneWeapon.setText(formatWeapon(playerOne));
        playerTwoWeapon.setText(formatWeapon(playerTwo));
        boolean hasPlayerThree = playerThree != null && scoreManager.getPlayerCount() > 2;
        playerThreePanel.setVisible(hasPlayerThree);
        playerThreePanel.setManaged(hasPlayerThree);
        if (hasPlayerThree) {
            playerThreeScore.setText("Score " + scoreManager.getScore(2));
            playerThreeWeapon.setText(formatWeapon(playerThree));
        }
        targetLabel.setText("First to " + scoreManager.getScoreToWin());
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
     * Creates a player score and weapon panel.
     *
     * @param name player label
     * @param scoreLabel score label to place
     * @param weaponLabel weapon label to place
     * @param accentColor CSS color for the player label
     * @return configured panel
     */
    private VBox createPlayerPanel(String name, Label scoreLabel, Label weaponLabel, String accentColor) {
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + accentColor + ";");
        scoreLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #1c2226;");
        weaponLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #39444a;");
        VBox box = new VBox(4, nameLabel, scoreLabel, weaponLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    /**
     * Creates the centered target score and message panel.
     *
     * @param mapName active map name
     * @return configured center panel
     */
    private VBox createCenterPanel(String mapName) {
        Label mapLabel = new Label(mapName);
        mapLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #586168;");
        targetLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: #20262b;");
        roundMessage.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #4a3333;");
        HBox targetBox = new HBox(targetLabel);
        targetBox.setAlignment(Pos.CENTER);
        VBox box = new VBox(2, mapLabel, targetBox, roundMessage);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Formats a tank's current powerup status.
     *
     * @param tank player tank
     * @return short weapon label
     */
    private String formatWeapon(PlayerTank tank) {
        String shield = tank.hasShield() ? " + Shield" : "";
        String health = "HP " + tank.getHp() + "/" + Tank.MAX_HP + " - ";
        if (tank.getEquippedPowerUp() == null) {
            return health + "Cannon" + shield;
        }
        return health + tank.getEquippedPowerUp().getDisplayName() + " x" + tank.getPowerUpAmmo() + shield;
    }
}
