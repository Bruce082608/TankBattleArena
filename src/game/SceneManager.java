package game;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import app.Main;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import maps.CityRuinsMap;
import maps.DesertFortressMap;
import maps.ForestBaseMap;
import maps.MapData;
import systems.ScoreManager;
import ui.GameOverScene;
import ui.InstructionScene;
import ui.MainMenu;
import ui.MapSelectionMenu;

/**
 * Owns top-level JavaFX scene changes for menus, gameplay, and game over.
 */
public class SceneManager {
    /** Score needed to end a match. */
    public static final int SCORE_TO_WIN = 5;

    private final Stage stage;
    private final List<MapData> maps;
    private MapData selectedMap;
    private int playerCount = 2;
    private GameManager activeGame;

    /**
     * Creates a scene manager for the primary stage.
     *
     * @param stage primary application stage
     */
    public SceneManager(Stage stage) {
        this.stage = stage;
        this.maps = List.of(new CityRuinsMap(), new ForestBaseMap(), new DesertFortressMap());
        this.selectedMap = maps.get(0);
    }

    /**
     * Displays the main menu.
     */
    public void showMainMenu() {
        stopActiveGame();
        setScene(new MainMenu(this).getView());
    }

    /**
     * Displays the map selection menu.
     */
    public void showMapSelection() {
        stopActiveGame();
        setScene(new MapSelectionMenu(this).getView());
    }

    /**
     * Displays the instructions screen.
     */
    public void showInstructions() {
        stopActiveGame();
        setScene(new InstructionScene(this).getView());
    }

    /**
     * Starts a fresh match on the selected map.
     */
    public void startGame() {
        launchGame(new ScoreManager(SCORE_TO_WIN, playerCount));
    }

    /**
     * Starts a fresh match on the currently selected map after game over.
     */
    public void restartMatch() {
        launchGame(new ScoreManager(SCORE_TO_WIN, playerCount));
    }

    /**
     * Continues the current match on a randomly selected new map.
     *
     * @param scoreManager current score state to preserve
     */
    public void continueMatchOnRandomMap(ScoreManager scoreManager) {
        selectedMap = chooseRandomRestartMap();
        launchGame(scoreManager);
    }

    /**
     * Shows the game-over scene for a completed match.
     *
     * @param scoreManager final score state
     */
    public void showGameOver(ScoreManager scoreManager) {
        stopActiveGame();
        setScene(new GameOverScene(this, selectedMap, scoreManager).getView());
    }

    /**
     * Selects a map for the next match.
     *
     * @param map map selected by the user
     */
    public void selectMap(MapData map) {
        selectedMap = map;
    }

    /**
     * Gets all playable maps.
     *
     * @return immutable map list
     */
    public List<MapData> getMaps() {
        return maps;
    }

    /**
     * Gets the selected map.
     *
     * @return selected map
     */
    public MapData getSelectedMap() {
        return selectedMap;
    }

    /**
     * Selects the local player count for the next match.
     *
     * @param playerCount local player count
     */
    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    /**
     * Gets the selected local player count.
     *
     * @return player count
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Starts gameplay with an existing score manager.
     *
     * @param scoreManager score state for the new gameplay scene
     */
    private void launchGame(ScoreManager scoreManager) {
        stopActiveGame();
        activeGame = new GameManager(this, selectedMap, scoreManager);
        Scene scene = activeGame.createScene();
        stage.setScene(scene);
        activeGame.start();
    }

    /**
     * Chooses a random restart map, avoiding the current map when alternatives exist.
     *
     * @return randomly chosen map for the next match
     */
    private MapData chooseRandomRestartMap() {
        if (maps.size() <= 1) {
            return selectedMap;
        }
        List<MapData> candidates = maps.stream()
                .filter(map -> map != selectedMap)
                .toList();
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    /**
     * Replaces the active scene with a menu scene.
     *
     * @param root menu root node
     */
    private void setScene(Parent root) {
        stage.setScene(new Scene(root, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT));
    }

    /**
     * Stops the gameplay loop when leaving gameplay.
     */
    private void stopActiveGame() {
        if (activeGame != null) {
            activeGame.stop();
            activeGame = null;
        }
    }
}
