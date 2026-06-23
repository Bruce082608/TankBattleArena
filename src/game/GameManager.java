package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import app.Main;
import entities.Bullet;
import entities.PlayerTank;
import entities.Tank;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import maps.MapData;
import obstacles.Obstacle;
import systems.CollisionManager;
import systems.ControlScheme;
import systems.InputHandler;
import systems.ScoreManager;
import systems.SoundManager;
import systems.TimerManager;
import ui.GameArt;
import ui.GameHUD;
import ui.PauseMenu;

/**
 * Coordinates the gameplay scene, round lifecycle, input, updates, and collisions.
 */
public class GameManager {
    /** Length of each round in seconds. */
    public static final double ROUND_LENGTH_SECONDS = 120;

    private static final double ROUND_RESET_DELAY_SECONDS = 1.35;
    private static final double GAME_OVER_DELAY_SECONDS = 1.65;

    private final SceneManager sceneManager;
    private final MapData mapData;
    private final ScoreManager scoreManager;
    private final TimerManager timerManager;
    private final InputHandler inputHandler;
    private final CollisionManager collisionManager;
    private final SoundManager soundManager;
    private final Pane arenaLayer;
    private final StackPane root;
    private final GameHUD hud;
    private final PauseMenu pauseMenu;
    private final List<Obstacle> obstacles;
    private final List<Bullet> bullets = new ArrayList<>();

    private PlayerTank playerOne;
    private PlayerTank playerTwo;
    private AnimationTimer gameLoop;
    private boolean paused;
    private boolean roundResetPending;
    private long lastFrameNanos;

    /**
     * Creates a gameplay controller for a map and score state.
     *
     * @param sceneManager scene switching service
     * @param mapData selected map
     * @param scoreManager match score state
     */
    public GameManager(SceneManager sceneManager, MapData mapData, ScoreManager scoreManager) {
        this.sceneManager = sceneManager;
        this.mapData = mapData;
        this.scoreManager = scoreManager;
        this.timerManager = new TimerManager(ROUND_LENGTH_SECONDS);
        this.inputHandler = new InputHandler();
        this.collisionManager = new CollisionManager();
        this.soundManager = new SoundManager();
        this.obstacles = mapData.createObstacles();
        this.arenaLayer = createArenaLayer();
        this.hud = new GameHUD(mapData.getName());
        this.pauseMenu = new PauseMenu(this::resumeGame, this::restartRound, sceneManager::showMainMenu);
        this.root = createRoot();
        createTanks();
        buildArena();
        updateHud();
    }

    /**
     * Creates the JavaFX scene for gameplay.
     *
     * @return gameplay scene
     */
    public Scene createScene() {
        Scene scene = new Scene(root, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        inputHandler.attachToScene(scene, this::togglePause);
        Platform.runLater(root::requestFocus);
        return scene;
    }

    /**
     * Starts the AnimationTimer game loop.
     */
    public void start() {
        gameLoop = new AnimationTimer() {
            /**
             * Processes a JavaFX animation frame.
             *
             * @param now current timestamp in nanoseconds
             */
            @Override
            public void handle(long now) {
                if (lastFrameNanos == 0) {
                    lastFrameNanos = now;
                    return;
                }
                double deltaSeconds = (now - lastFrameNanos) / 1_000_000_000.0;
                lastFrameNanos = now;
                if (!paused) {
                    update(deltaSeconds, now);
                }
            }
        };
        gameLoop.start();
    }

    /**
     * Stops the game loop.
     */
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    /**
     * Advances gameplay by one frame.
     *
     * @param deltaSeconds elapsed time since the previous frame
     * @param nowNanos current AnimationTimer timestamp
     */
    private void update(double deltaSeconds, long nowNanos) {
        if (roundResetPending) {
            return;
        }
        timerManager.update(deltaSeconds);
        playerOne.handleInput(deltaSeconds, inputHandler, collisionManager, obstacles, mapData);
        playerTwo.handleInput(deltaSeconds, inputHandler, collisionManager, obstacles, mapData);
        handleShooting(nowNanos);
        updateBullets(deltaSeconds);
        if (timerManager.isExpired()) {
            concludeTimedRound();
        }
        updateHud();
    }

    /**
     * Creates the fixed-size arena layer.
     *
     * @return arena pane
     */
    private Pane createArenaLayer() {
        Pane pane = new Pane();
        pane.setPrefSize(mapData.getWidth(), mapData.getHeight());
        pane.setMinSize(mapData.getWidth(), mapData.getHeight());
        pane.setMaxSize(mapData.getWidth(), mapData.getHeight());
        pane.setClip(new Rectangle(mapData.getWidth(), mapData.getHeight()));
        pane.setStyle("-fx-background-color: linear-gradient(to bottom, #29343a, #1f272b);"
                + "-fx-border-color: #d9b85f; -fx-border-width: 4;");
        return pane;
    }

    /**
     * Creates the root layout with HUD, arena, and pause overlay.
     *
     * @return gameplay root
     */
    private StackPane createRoot() {
        BorderPane frame = new BorderPane();
        frame.setTop(hud);
        StackPane arenaWrapper = new StackPane(arenaLayer);
        arenaWrapper.setStyle("-fx-background-color: #11171a;");
        frame.setCenter(arenaWrapper);
        StackPane stack = new StackPane(frame, pauseMenu);
        stack.setFocusTraversable(true);
        pauseMenu.setVisible(false);
        return stack;
    }

    /**
     * Creates both player tanks at the selected map spawn points.
     */
    private void createTanks() {
        ControlScheme playerOneControls = new ControlScheme(
                KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D, KeyCode.SPACE);
        ControlScheme playerTwoControls = new ControlScheme(
                KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER);
        Point2D p1Spawn = mapData.getPlayerOneSpawn();
        Point2D p2Spawn = mapData.getPlayerTwoSpawn();
        playerOne = new PlayerTank(
                "Player 1",
                p1Spawn.getX(),
                p1Spawn.getY(),
                Color.DODGERBLUE,
                Color.LIGHTCYAN,
                Color.rgb(99, 190, 255),
                playerOneControls);
        playerTwo = new PlayerTank(
                "Player 2",
                p2Spawn.getX(),
                p2Spawn.getY(),
                Color.CRIMSON,
                Color.MISTYROSE,
                Color.rgb(255, 113, 107),
                playerTwoControls);
        playerOne.setRotationDegrees(mapData.getPlayerOneRotation());
        playerTwo.setRotationDegrees(mapData.getPlayerTwoRotation());
    }

    /**
     * Places obstacles and tanks on the arena layer.
     */
    private void buildArena() {
        arenaLayer.getChildren().clear();
        obstacles.forEach(obstacle -> arenaLayer.getChildren().add(obstacle.render()));
        arenaLayer.getChildren().addAll(playerOne.render(), playerTwo.render());
    }

    /**
     * Fires bullets for players whose shoot key is pressed.
     *
     * @param nowNanos current AnimationTimer timestamp
     */
    private void handleShooting(long nowNanos) {
        maybeFire(playerOne, nowNanos);
        maybeFire(playerTwo, nowNanos);
    }

    /**
     * Attempts to fire a bullet for one tank.
     *
     * @param tank tank that may shoot
     * @param nowNanos current timestamp
     */
    private void maybeFire(PlayerTank tank, long nowNanos) {
        if (!inputHandler.isPressed(tank.getControls().shoot())) {
            return;
        }
        Bullet bullet = tank.tryShoot(nowNanos);
        if (bullet != null) {
            bullets.add(bullet);
            arenaLayer.getChildren().add(bullet.render());
            soundManager.playShoot();
        }
    }

    /**
     * Updates bullets and handles bullet collisions.
     *
     * @param deltaSeconds elapsed time since the previous frame
     */
    private void updateBullets(double deltaSeconds) {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            if (roundResetPending) {
                break;
            }
            Bullet bullet = iterator.next();
            bullet.update(deltaSeconds);
            boolean remove = bullet.isExpired();
            if (!remove) {
                remove = handleBulletBounce(bullet);
            }
            if (!remove) {
                remove = handleTankHit(bullet);
            }
            if (remove) {
                arenaLayer.getChildren().remove(bullet.getView());
                iterator.remove();
            }
        }
    }

    /**
     * Bounces a bullet off arena edges or blocking obstacles until its bounce budget is spent.
     *
     * @param bullet bullet to test
     * @return true when the bullet should be removed
     */
    private boolean handleBulletBounce(Bullet bullet) {
        boolean outsideArena = collisionManager.isOutsideArena(bullet, mapData);
        Optional<Obstacle> obstacleHit = collisionManager.findObstacleCollision(bullet, obstacles);
        if (!outsideArena && obstacleHit.isEmpty()) {
            return false;
        }
        if (!bullet.hasBouncesRemaining()) {
            return true;
        }

        boolean horizontal = false;
        boolean vertical = false;
        if (outsideArena) {
            Rectangle2D bulletBox = bullet.getCollisionBox();
            horizontal = bulletBox.getMinX() < 0 || bulletBox.getMaxX() > mapData.getWidth();
            vertical = bulletBox.getMinY() < 0 || bulletBox.getMaxY() > mapData.getHeight();
        }
        if (obstacleHit.isPresent()) {
            boolean[] axes = determineObstacleBounceAxes(bullet, obstacleHit.get());
            horizontal = horizontal || axes[0];
            vertical = vertical || axes[1];
        }
        if (!horizontal && !vertical) {
            horizontal = Math.abs(bullet.getVelocityX()) >= Math.abs(bullet.getVelocityY());
            vertical = !horizontal;
        }

        bullet.rewindToPreviousPosition();
        bullet.bounce(horizontal, vertical);
        clampBulletInsideArena(bullet);
        return false;
    }

    /**
     * Determines which velocity axis should flip after an obstacle impact.
     *
     * @param bullet colliding bullet
     * @param obstacle obstacle that was hit
     * @return two flags: horizontal reflection, vertical reflection
     */
    private boolean[] determineObstacleBounceAxes(Bullet bullet, Obstacle obstacle) {
        Rectangle2D previousBox = bullet.getPreviousCollisionBox();
        Rectangle2D currentBox = bullet.getCollisionBox();
        Rectangle2D obstacleBox = obstacle.getCollisionBox();

        boolean horizontal = previousBox.getMaxX() <= obstacleBox.getMinX()
                || previousBox.getMinX() >= obstacleBox.getMaxX();
        boolean vertical = previousBox.getMaxY() <= obstacleBox.getMinY()
                || previousBox.getMinY() >= obstacleBox.getMaxY();
        if (horizontal || vertical) {
            return new boolean[] { horizontal, vertical };
        }

        double leftOverlap = currentBox.getMaxX() - obstacleBox.getMinX();
        double rightOverlap = obstacleBox.getMaxX() - currentBox.getMinX();
        double topOverlap = currentBox.getMaxY() - obstacleBox.getMinY();
        double bottomOverlap = obstacleBox.getMaxY() - currentBox.getMinY();
        double horizontalOverlap = Math.min(leftOverlap, rightOverlap);
        double verticalOverlap = Math.min(topOverlap, bottomOverlap);

        if (Math.abs(horizontalOverlap - verticalOverlap) < 0.01) {
            return new boolean[] { true, true };
        }
        return horizontalOverlap < verticalOverlap
                ? new boolean[] { true, false }
                : new boolean[] { false, true };
    }

    /**
     * Keeps a bounced bullet inside the arena before its next update tick.
     *
     * @param bullet bullet to clamp
     */
    private void clampBulletInsideArena(Bullet bullet) {
        double clampedX = Math.max(0, Math.min(mapData.getWidth() - bullet.getWidth(), bullet.getX()));
        double clampedY = Math.max(0, Math.min(mapData.getHeight() - bullet.getHeight(), bullet.getY()));
        bullet.setPosition(clampedX, clampedY);
    }

    /**
     * Applies bullet damage when a projectile hits the opposing tank.
     *
     * @param bullet bullet to test
     * @return true when the bullet should be removed
     */
    private boolean handleTankHit(Bullet bullet) {
        PlayerTank target = bullet.getOwner() == playerOne ? playerTwo : playerOne;
        if (!collisionManager.collides(bullet, target)) {
            return false;
        }
        target.takeDamage(bullet.getDamage());
        playHitEffect(target);
        if (target.isDestroyed()) {
            concludeDestroyedTankRound(target);
        }
        return true;
    }

    /**
     * Concludes a round because one tank was destroyed.
     *
     * @param destroyedTank tank whose HP reached zero
     */
    private void concludeDestroyedTankRound(PlayerTank destroyedTank) {
        if (roundResetPending) {
            return;
        }
        RoundResult result = destroyedTank == playerOne ? RoundResult.PLAYER_TWO_WIN : RoundResult.PLAYER_ONE_WIN;
        concludeRound(result, destroyedTank);
    }

    /**
     * Concludes a round when the timer expires.
     */
    private void concludeTimedRound() {
        if (roundResetPending) {
            return;
        }
        RoundResult result;
        if (playerOne.getHp() > playerTwo.getHp()) {
            result = RoundResult.PLAYER_ONE_WIN;
        } else if (playerTwo.getHp() > playerOne.getHp()) {
            result = RoundResult.PLAYER_TWO_WIN;
        } else {
            result = RoundResult.DRAW;
        }
        concludeRound(result, null);
    }

    /**
     * Updates scores, plays effects, and schedules the next state.
     *
     * @param result round result
     * @param destroyedTank destroyed tank, or null for timer results
     */
    private void concludeRound(RoundResult result, PlayerTank destroyedTank) {
        roundResetPending = true;
        inputHandler.clear();
        if (result == RoundResult.PLAYER_ONE_WIN) {
            scoreManager.addPlayerOnePoint();
            hud.showRoundMessage("Player 1 wins the round");
        } else if (result == RoundResult.PLAYER_TWO_WIN) {
            scoreManager.addPlayerTwoPoint();
            hud.showRoundMessage("Player 2 wins the round");
        } else {
            hud.showRoundMessage("Round draw");
        }
        if (destroyedTank != null) {
            destroyedTank.getView().setOpacity(0.25);
            playExplosion(destroyedTank);
        }
        updateHud();
        if (scoreManager.hasWinner()) {
            soundManager.playVictory();
            scheduleGameOver();
        } else {
            scheduleRoundReset();
        }
    }

    /**
     * Schedules the next round after a short animation delay.
     */
    private void scheduleRoundReset() {
        PauseTransition delay = new PauseTransition(Duration.seconds(ROUND_RESET_DELAY_SECONDS));
        delay.setOnFinished(event -> resetRound());
        delay.play();
    }

    /**
     * Schedules the game-over scene after a short animation delay.
     */
    private void scheduleGameOver() {
        PauseTransition delay = new PauseTransition(Duration.seconds(GAME_OVER_DELAY_SECONDS));
        delay.setOnFinished(event -> sceneManager.showGameOver(scoreManager));
        delay.play();
    }

    /**
     * Resets tanks, bullets, timer, and input state for the current map.
     */
    private void resetRound() {
        removeBullets();
        playerOne.resetTo(
                mapData.getPlayerOneSpawn().getX(),
                mapData.getPlayerOneSpawn().getY(),
                mapData.getPlayerOneRotation());
        playerTwo.resetTo(
                mapData.getPlayerTwoSpawn().getX(),
                mapData.getPlayerTwoSpawn().getY(),
                mapData.getPlayerTwoRotation());
        timerManager.reset();
        hud.clearRoundMessage();
        paused = false;
        roundResetPending = false;
        lastFrameNanos = 0;
        inputHandler.clear();
        pauseMenu.setVisible(false);
        updateHud();
        Platform.runLater(root::requestFocus);
    }

    /**
     * Restarts only the current round without changing score.
     */
    private void restartRound() {
        resetRound();
    }

    /**
     * Toggles the pause overlay.
     */
    private void togglePause() {
        if (roundResetPending) {
            return;
        }
        paused = !paused;
        pauseMenu.setVisible(paused);
        inputHandler.clear();
        lastFrameNanos = 0;
        Platform.runLater(root::requestFocus);
    }

    /**
     * Resumes gameplay from the pause menu.
     */
    private void resumeGame() {
        paused = false;
        pauseMenu.setVisible(false);
        inputHandler.clear();
        lastFrameNanos = 0;
        Platform.runLater(root::requestFocus);
    }

    /**
     * Removes every active bullet from the arena.
     */
    private void removeBullets() {
        bullets.forEach(bullet -> arenaLayer.getChildren().remove(bullet.getView()));
        bullets.clear();
    }

    /**
     * Plays an animated explosion at a destroyed tank.
     *
     * @param tank tank location for the effect
     */
    private void playExplosion(Tank tank) {
        ImageView explosion = new ImageView(GameArt.createExplosionImage());
        explosion.setFitWidth(110);
        explosion.setFitHeight(110);
        explosion.setLayoutX(tank.getCenterX() - 55);
        explosion.setLayoutY(tank.getCenterY() - 55);
        arenaLayer.getChildren().add(explosion);
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.7), explosion);
        scale.setFromX(0.4);
        scale.setFromY(0.4);
        scale.setToX(1.35);
        scale.setToY(1.35);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.7), explosion);
        fade.setFromValue(1);
        fade.setToValue(0);
        ParallelTransition animation = new ParallelTransition(scale, fade);
        animation.setOnFinished(event -> arenaLayer.getChildren().remove(explosion));
        animation.play();
        soundManager.playExplosion();
    }

    /**
     * Plays a compact hit effect at the target tank.
     *
     * @param tank tank that was hit
     */
    private void playHitEffect(Tank tank) {
        ImageView hit = new ImageView(GameArt.createHitImage());
        hit.setFitWidth(42);
        hit.setFitHeight(42);
        hit.setLayoutX(tank.getCenterX() - 21);
        hit.setLayoutY(tank.getCenterY() - 21);
        arenaLayer.getChildren().add(hit);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.25), hit);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(event -> arenaLayer.getChildren().remove(hit));
        fade.play();
    }

    /**
     * Refreshes the HUD labels and health bars.
     */
    private void updateHud() {
        hud.update(scoreManager, timerManager, playerOne, playerTwo);
    }

    /**
     * Describes the possible round outcomes.
     */
    private enum RoundResult {
        PLAYER_ONE_WIN,
        PLAYER_TWO_WIN,
        DRAW
    }
}
