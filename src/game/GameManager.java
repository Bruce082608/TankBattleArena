package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import app.Main;
import entities.Bullet;
import entities.Mine;
import entities.PlayerTank;
import entities.PowerUp;
import entities.Tank;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import maps.MapData;
import obstacles.Obstacle;
import systems.CollisionManager;
import systems.ControlScheme;
import systems.InputHandler;
import systems.PowerUpType;
import systems.ScoreManager;
import systems.SoundManager;
import ui.GameArt;
import ui.GameHUD;
import ui.PauseMenu;

/**
 * Coordinates classic local ricochet-tank gameplay, rounds, pickups, and collisions.
 */
public class GameManager {
    private static final double ROUND_RESET_DELAY_SECONDS = 1.35;
    private static final double GAME_OVER_DELAY_SECONDS = 1.65;
    private static final double POWERUP_SPAWN_INTERVAL_SECONDS = 7.0;
    private static final int MAX_POWERUPS = 3;
    private static final int MAX_PLAYER_BULLETS = 5;
    private static final int LASER_DAMAGE = 3;
    private static final int LASER_MAX_REFLECTIONS = 4;
    private static final double LASER_STEP_PIXELS = 6.0;
    private static final double LASER_MAX_DISTANCE = 1800.0;
    private static final double LASER_PREVIEW_OPACITY = 0.42;
    private static final double LASER_BEAM_SECONDS = 0.22;
    private static final long GATLING_COOLDOWN_NANOS = 90_000_000L;
    private static final long MINE_COOLDOWN_NANOS = 550_000_000L;
    private static final long SPECIAL_COOLDOWN_NANOS = 360_000_000L;
    private static final int PLAYER_THREE_MODE_THRESHOLD = 3;

    private final SceneManager sceneManager;
    private final MapData mapData;
    private final ScoreManager scoreManager;
    private final InputHandler inputHandler;
    private final CollisionManager collisionManager;
    private final SoundManager soundManager;
    private final Pane arenaLayer;
    private final StackPane root;
    private final GameHUD hud;
    private final PauseMenu pauseMenu;
    private final List<Obstacle> obstacles;
    private final List<PlayerTank> players = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Mine> mines = new ArrayList<>();
    private final List<Line> laserPreviewLines = new ArrayList<>();
    private final Random random = new Random();

    private AnimationTimer gameLoop;
    private boolean paused;
    private boolean roundResetPending;
    private long lastFrameNanos;
    private double powerUpSpawnTimer = 2.0;

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
        updatePlayers(deltaSeconds);
        handleShooting(nowNanos);
        updateBullets(deltaSeconds);
        updateMines(deltaSeconds);
        updatePowerUps(deltaSeconds);
        updateLaserPreview();
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
        pane.setStyle("-fx-background-color: #f5f2e9;"
                + "-fx-border-color: #1f2529; -fx-border-width: 4;");
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
        arenaWrapper.setStyle("-fx-background-color: #d8d2c3;");
        frame.setCenter(arenaWrapper);
        StackPane stack = new StackPane(frame, pauseMenu);
        stack.setFocusTraversable(true);
        pauseMenu.setVisible(false);
        return stack;
    }

    /**
     * Creates local player tanks at classic two-player spawns, plus optional mouse player.
     */
    private void createTanks() {
        ControlScheme playerOneControls = new ControlScheme(
                KeyCode.E, KeyCode.D, KeyCode.S, KeyCode.F, KeyCode.Q);
        ControlScheme playerTwoControls = new ControlScheme(
                KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.M);

        Point2D p1Spawn = mapData.getPlayerOneSpawn();
        Point2D p2Spawn = mapData.getPlayerTwoSpawn();
        PlayerTank playerOne = new PlayerTank(
                "Player 1",
                p1Spawn.getX(),
                p1Spawn.getY(),
                Color.DODGERBLUE,
                Color.LIGHTCYAN,
                Color.rgb(41, 106, 226),
                playerOneControls);
        PlayerTank playerTwo = new PlayerTank(
                "Player 2",
                p2Spawn.getX(),
                p2Spawn.getY(),
                Color.CRIMSON,
                Color.MISTYROSE,
                Color.rgb(218, 51, 57),
                playerTwoControls);
        playerOne.setRotationDegrees(mapData.getPlayerOneRotation());
        playerTwo.setRotationDegrees(mapData.getPlayerTwoRotation());
        players.add(playerOne);
        players.add(playerTwo);

        if (scoreManager.getPlayerCount() >= PLAYER_THREE_MODE_THRESHOLD) {
            Point2D spawn = findOpenSpawn();
            PlayerTank playerThree = new PlayerTank(
                    "Player 3",
                    spawn.getX(),
                    spawn.getY(),
                    Color.rgb(42, 170, 82),
                    Color.rgb(214, 255, 223),
                    Color.rgb(42, 170, 82),
                    new ControlScheme(KeyCode.UNDEFINED, KeyCode.UNDEFINED, KeyCode.UNDEFINED,
                            KeyCode.UNDEFINED, KeyCode.UNDEFINED));
            playerThree.setRotationDegrees(270);
            players.add(playerThree);
        }
    }

    /**
     * Places obstacles and tanks on the arena layer.
     */
    private void buildArena() {
        arenaLayer.getChildren().clear();
        obstacles.forEach(obstacle -> arenaLayer.getChildren().add(obstacle.render()));
        players.forEach(player -> arenaLayer.getChildren().add(player.render()));
    }

    /**
     * Updates keyboard and mouse-controlled tanks.
     *
     * @param deltaSeconds elapsed time since previous frame
     */
    private void updatePlayers(double deltaSeconds) {
        for (int i = 0; i < players.size(); i++) {
            PlayerTank player = players.get(i);
            if (player.isDestroyed()) {
                continue;
            }
            if (i == 2) {
                updateMousePlayer(player, deltaSeconds);
            } else {
                player.handleInput(deltaSeconds, inputHandler, collisionManager, obstacles, mapData);
            }
        }
    }

    /**
     * Moves player three toward the mouse and aims at it.
     *
     * @param player mouse-controlled player
     * @param deltaSeconds elapsed time since previous frame
     */
    private void updateMousePlayer(PlayerTank player, double deltaSeconds) {
        if (!inputHandler.hasMousePosition()) {
            return;
        }
        Bounds arenaBounds = arenaLayer.localToScene(arenaLayer.getBoundsInLocal());
        double targetX = inputHandler.getMouseX() - arenaBounds.getMinX();
        double targetY = inputHandler.getMouseY() - arenaBounds.getMinY();
        double dx = targetX - player.getCenterX();
        double dy = targetY - player.getCenterY();
        if (Math.hypot(dx, dy) < 6) {
            return;
        }
        player.setRotationDegrees(Math.toDegrees(Math.atan2(dy, dx)));
        player.move(1, deltaSeconds, collisionManager, obstacles, mapData);
    }

    /**
     * Fires bullets or uses equipped weapons for players whose shoot input is pressed.
     *
     * @param nowNanos current AnimationTimer timestamp
     */
    private void handleShooting(long nowNanos) {
        for (int i = 0; i < players.size(); i++) {
            PlayerTank player = players.get(i);
            if (player.isDestroyed()) {
                continue;
            }
            boolean wantsShoot = i == 2
                    ? inputHandler.isPrimaryMousePressed()
                    : inputHandler.isPressed(player.getControls().shoot());
            if (wantsShoot) {
                fireWeapon(player, nowNanos);
            }
        }
    }

    /**
     * Fires the tank's equipped weapon, or its regular cannon.
     *
     * @param player firing player
     * @param nowNanos current timestamp
     */
    private void fireWeapon(PlayerTank player, long nowNanos) {
        PowerUpType type = player.getEquippedPowerUp();
        if (type == null) {
            if (countBulletsFor(player) >= MAX_PLAYER_BULLETS) {
                return;
            }
            Bullet bullet = player.tryShoot(nowNanos);
            if (bullet != null) {
                addBullet(bullet);
            }
            return;
        }

        switch (type) {
            case GATLING -> fireGatling(player, nowNanos);
            case MINE -> placeMine(player, nowNanos);
            case LASER -> fireLaser(player, nowNanos);
            case SHOTGUN -> fireShotgun(player, nowNanos);
            case SHIELD -> player.clearPowerUp();
            case HOMING_MISSILE -> fireHomingMissile(player, nowNanos);
        }
    }

    /**
     * Fires a fast stream of low-spread bullets.
     *
     * @param player firing player
     * @param nowNanos current timestamp
     */
    private void fireGatling(PlayerTank player, long nowNanos) {
        if (!player.canShoot(nowNanos, GATLING_COOLDOWN_NANOS)) {
            return;
        }
        double spread = random.nextDouble(-4.0, 4.0);
        Bullet bullet = player.createBullet(
                player.getRotationDegrees() + spread,
                540,
                5,
                4.5,
                Tank.BULLET_DAMAGE);
        addBullet(bullet);
        player.consumePowerUpAmmo();
    }

    /**
     * Drops a mine behind the tank.
     *
     * @param player mine owner
     * @param nowNanos current timestamp
     */
    private void placeMine(PlayerTank player, long nowNanos) {
        if (!player.canShoot(nowNanos, MINE_COOLDOWN_NANOS)) {
            return;
        }
        double radians = Math.toRadians(player.getRotationDegrees());
        double x = player.getCenterX() - Math.cos(radians) * (player.getWidth() / 2.0 + 12) - Mine.SIZE / 2.0;
        double y = player.getCenterY() - Math.sin(radians) * (player.getHeight() / 2.0 + 12) - Mine.SIZE / 2.0;
        Mine mine = new Mine(player, x, y, player.getBulletColor());
        mines.add(mine);
        arenaLayer.getChildren().add(mine.render());
        player.consumePowerUpAmmo();
    }

    /**
     * Fires a reflected instant laser beam along the preview path.
     *
     * @param player firing player
     * @param nowNanos current timestamp
     */
    private void fireLaser(PlayerTank player, long nowNanos) {
        if (!player.canShoot(nowNanos, SPECIAL_COOLDOWN_NANOS)) {
            return;
        }
        List<LineSegment> path = traceLaserPath(player);
        drawLaserBeam(path);
        applyLaserDamage(player, path);
        soundManager.playShoot();
        player.consumePowerUpAmmo();
    }

    /**
     * Fires a spread of ricocheting pellets.
     *
     * @param player firing player
     * @param nowNanos current timestamp
     */
    private void fireShotgun(PlayerTank player, long nowNanos) {
        if (!player.canShoot(nowNanos, SPECIAL_COOLDOWN_NANOS)) {
            return;
        }
        for (double offset : List.of(-18.0, -9.0, 0.0, 9.0, 18.0)) {
            addBullet(player.createBullet(
                    player.getRotationDegrees() + offset,
                    504,
                    3,
                    2.4,
                    Tank.BULLET_DAMAGE));
        }
        player.consumePowerUpAmmo();
    }

    /**
     * Fires a gently turning missile at the nearest living opponent.
     *
     * @param player firing player
     * @param nowNanos current timestamp
     */
    private void fireHomingMissile(PlayerTank player, long nowNanos) {
        if (!player.canShoot(nowNanos, SPECIAL_COOLDOWN_NANOS)) {
            return;
        }
        Bullet bullet = player.createBullet(
                player.getRotationDegrees(),
                351,
                4,
                6.0,
                Tank.BULLET_DAMAGE);
        bullet.setHomingTarget(findNearestOpponent(player));
        addBullet(bullet);
        player.consumePowerUpAmmo();
    }

    /**
     * Adds a projectile to the arena.
     *
     * @param bullet projectile
     */
    private void addBullet(Bullet bullet) {
        bullets.add(bullet);
        arenaLayer.getChildren().add(bullet.render());
        soundManager.playShoot();
    }

    /**
     * Refreshes the reflected aim preview for players holding the laser pickup.
     */
    private void updateLaserPreview() {
        clearLaserPreview();
        if (roundResetPending || paused) {
            return;
        }
        for (PlayerTank player : players) {
            if (player.isDestroyed() || player.getEquippedPowerUp() != PowerUpType.LASER) {
                continue;
            }
            for (LineSegment segment : traceLaserPath(player)) {
                Line line = new Line(segment.startX(), segment.startY(), segment.endX(), segment.endY());
                line.setStroke(player.getBulletColor());
                line.setStrokeWidth(2.0);
                line.setOpacity(LASER_PREVIEW_OPACITY);
                line.getStrokeDashArray().addAll(12.0, 10.0);
                line.setMouseTransparent(true);
                laserPreviewLines.add(line);
                arenaLayer.getChildren().add(line);
            }
        }
    }

    /**
     * Removes visible laser preview lines.
     */
    private void clearLaserPreview() {
        laserPreviewLines.forEach(line -> arenaLayer.getChildren().remove(line));
        laserPreviewLines.clear();
    }

    /**
     * Traces a laser path that reflects from walls and blocking obstacles.
     *
     * @param player source tank
     * @return reflected laser path segments
     */
    private List<LineSegment> traceLaserPath(PlayerTank player) {
        List<LineSegment> segments = new ArrayList<>();
        double radians = Math.toRadians(player.getRotationDegrees());
        double x = player.getCenterX() + Math.cos(radians) * (player.getWidth() / 2.0 + 8);
        double y = player.getCenterY() + Math.sin(radians) * (player.getWidth() / 2.0 + 8);
        double dx = Math.cos(radians);
        double dy = Math.sin(radians);
        double remainingDistance = LASER_MAX_DISTANCE;

        for (int bounce = 0; bounce <= LASER_MAX_REFLECTIONS && remainingDistance > 0; bounce++) {
            double startX = x;
            double startY = y;
            LaserImpact impact = findLaserImpact(x, y, dx, dy, remainingDistance);
            segments.add(new LineSegment(startX, startY, impact.x(), impact.y()));
            remainingDistance -= distance(startX, startY, impact.x(), impact.y());
            if (impact.axis() == BounceAxis.NONE) {
                break;
            }
            x = impact.x() - dx * 0.5;
            y = impact.y() - dy * 0.5;
            if (impact.axis() == BounceAxis.HORIZONTAL || impact.axis() == BounceAxis.BOTH) {
                dx = -dx;
            }
            if (impact.axis() == BounceAxis.VERTICAL || impact.axis() == BounceAxis.BOTH) {
                dy = -dy;
            }
            x += dx * 1.5;
            y += dy * 1.5;
        }
        return segments;
    }

    /**
     * Walks the laser ray until it reaches a wall, obstacle, or max length.
     *
     * @param x ray start x
     * @param y ray start y
     * @param dx normalized x direction
     * @param dy normalized y direction
     * @param maxDistance remaining ray length
     * @return impact point and reflection axis
     */
    private LaserImpact findLaserImpact(double x, double y, double dx, double dy, double maxDistance) {
        double previousX = x;
        double previousY = y;
        double traveled = 0;
        while (traveled < maxDistance) {
            double step = Math.min(LASER_STEP_PIXELS, maxDistance - traveled);
            double nextX = previousX + dx * step;
            double nextY = previousY + dy * step;
            traveled += step;

            boolean outsideX = nextX <= 0 || nextX >= mapData.getWidth();
            boolean outsideY = nextY <= 0 || nextY >= mapData.getHeight();
            Optional<Obstacle> obstacle = findLaserObstacle(nextX, nextY);
            if (!outsideX && !outsideY && obstacle.isEmpty()) {
                previousX = nextX;
                previousY = nextY;
                continue;
            }

            double impactX = Math.max(0, Math.min(mapData.getWidth(), nextX));
            double impactY = Math.max(0, Math.min(mapData.getHeight(), nextY));
            BounceAxis axis = determineLaserBounceAxis(previousX, previousY, impactX, impactY, outsideX, outsideY,
                    obstacle.orElse(null));
            return new LaserImpact(impactX, impactY, axis);
        }
        return new LaserImpact(previousX, previousY, BounceAxis.NONE);
    }

    /**
     * Finds an obstacle containing the laser point.
     *
     * @param x point x
     * @param y point y
     * @return obstacle hit
     */
    private Optional<Obstacle> findLaserObstacle(double x, double y) {
        return obstacles.stream()
                .filter(Obstacle::blocksBullets)
                .filter(obstacle -> obstacle.getCollisionBox().contains(x, y))
                .findFirst();
    }

    /**
     * Determines the laser reflection axis.
     *
     * @param previousX previous ray x
     * @param previousY previous ray y
     * @param impactX impact x
     * @param impactY impact y
     * @param outsideX whether an arena side was hit
     * @param outsideY whether an arena top/bottom was hit
     * @param obstacle obstacle hit, if any
     * @return reflection axis
     */
    private BounceAxis determineLaserBounceAxis(
            double previousX,
            double previousY,
            double impactX,
            double impactY,
            boolean outsideX,
            boolean outsideY,
            Obstacle obstacle) {
        boolean horizontal = outsideX;
        boolean vertical = outsideY;
        if (obstacle != null) {
            Rectangle2D box = obstacle.getCollisionBox();
            horizontal = horizontal || previousX <= box.getMinX() && impactX >= box.getMinX()
                    || previousX >= box.getMaxX() && impactX <= box.getMaxX();
            vertical = vertical || previousY <= box.getMinY() && impactY >= box.getMinY()
                    || previousY >= box.getMaxY() && impactY <= box.getMaxY();
        }
        if (horizontal && vertical) {
            return BounceAxis.BOTH;
        }
        if (horizontal) {
            return BounceAxis.HORIZONTAL;
        }
        if (vertical) {
            return BounceAxis.VERTICAL;
        }
        return BounceAxis.HORIZONTAL;
    }

    /**
     * Draws the fired laser as a bright short-lived beam.
     *
     * @param path reflected beam path
     */
    private void drawLaserBeam(List<LineSegment> path) {
        List<Line> beamLines = new ArrayList<>();
        for (LineSegment segment : path) {
            Line glow = new Line(segment.startX(), segment.startY(), segment.endX(), segment.endY());
            glow.setStroke(Color.rgb(100, 235, 255, 0.55));
            glow.setStrokeWidth(14);
            glow.setMouseTransparent(true);
            Line core = new Line(segment.startX(), segment.startY(), segment.endX(), segment.endY());
            core.setStroke(Color.WHITE);
            core.setStrokeWidth(4);
            core.setEffect(new DropShadow(18, Color.rgb(80, 224, 255, 0.95)));
            core.setMouseTransparent(true);
            beamLines.add(glow);
            beamLines.add(core);
            arenaLayer.getChildren().addAll(glow, core);
        }
        ParallelTransition animation = new ParallelTransition();
        for (Line line : beamLines) {
            FadeTransition lineFade = new FadeTransition(Duration.seconds(LASER_BEAM_SECONDS), line);
            lineFade.setFromValue(1);
            lineFade.setToValue(0);
            animation.getChildren().add(lineFade);
        }
        animation.setOnFinished(event -> beamLines.forEach(line -> arenaLayer.getChildren().remove(line)));
        animation.play();
    }

    /**
     * Deals laser damage to every tank touched by the beam.
     *
     * @param source firing tank
     * @param path reflected laser path
     */
    private void applyLaserDamage(PlayerTank source, List<LineSegment> path) {
        for (PlayerTank target : players) {
            if (target.isDestroyed()) {
                continue;
            }
            double skipDistance = target == source ? Bullet.OWNER_SAFE_SECONDS * Bullet.SPEED : 0;
            if (laserPathIntersectsTank(path, target, skipDistance)) {
                hitTank(target, LASER_DAMAGE);
            }
        }
    }

    /**
     * Tests whether a reflected laser path crosses a tank.
     *
     * @param path laser path
     * @param target tank to test
     * @param skipDistance distance to ignore from the muzzle
     * @return true when hit
     */
    private boolean laserPathIntersectsTank(List<LineSegment> path, PlayerTank target, double skipDistance) {
        double skipped = 0;
        for (LineSegment segment : path) {
            double length = distance(segment.startX(), segment.startY(), segment.endX(), segment.endY());
            if (skipped + length <= skipDistance) {
                skipped += length;
                continue;
            }
            Line line = new Line(segment.startX(), segment.startY(), segment.endX(), segment.endY());
            Shape intersection = Shape.intersect(line, new Rectangle(
                    target.getCollisionBox().getMinX(),
                    target.getCollisionBox().getMinY(),
                    target.getCollisionBox().getWidth(),
                    target.getCollisionBox().getHeight()));
            if (intersection.getBoundsInLocal().getWidth() > 0 || intersection.getBoundsInLocal().getHeight() > 0) {
                return true;
            }
            skipped += length;
        }
        return false;
    }

    /**
     * Counts active bullets owned by a tank.
     *
     * @param player owner
     * @return active bullet count
     */
    private long countBulletsFor(PlayerTank player) {
        return bullets.stream().filter(bullet -> bullet.getOwner() == player).count();
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
     * Applies bullet damage when a projectile hits any living tank, including its owner.
     *
     * @param bullet bullet to test
     * @return true when the bullet should be removed
     */
    private boolean handleTankHit(Bullet bullet) {
        for (PlayerTank player : players) {
            if (player.isDestroyed()
                    || player == bullet.getOwner() && !bullet.canHitOwner()
                    || !collisionManager.collides(bullet, player)) {
                continue;
            }
                hitTank(player, Tank.BULLET_DAMAGE);
                return true;
        }
        return false;
    }

    /**
     * Updates mines and checks armed-mine impacts.
     *
     * @param deltaSeconds elapsed time since previous frame
     */
    private void updateMines(double deltaSeconds) {
        Iterator<Mine> iterator = mines.iterator();
        while (iterator.hasNext()) {
            Mine mine = iterator.next();
            mine.update(deltaSeconds);
            if (!mine.isArmed()) {
                continue;
            }
            for (PlayerTank player : players) {
                if (player.isDestroyed() || !collisionManager.collides(mine, player)) {
                    continue;
                }
                arenaLayer.getChildren().remove(mine.getView());
                iterator.remove();
                hitTank(player, Tank.BULLET_DAMAGE);
                return;
            }
        }
    }

    /**
     * Spawns and collects random pickups.
     *
     * @param deltaSeconds elapsed time since previous frame
     */
    private void updatePowerUps(double deltaSeconds) {
        powerUpSpawnTimer -= deltaSeconds;
        if (powerUpSpawnTimer <= 0 && powerUps.size() < MAX_POWERUPS) {
            spawnPowerUp();
            powerUpSpawnTimer = POWERUP_SPAWN_INTERVAL_SECONDS;
        }

        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            for (PlayerTank player : players) {
                if (player.isDestroyed() || !collisionManager.collides(powerUp, player)) {
                    continue;
                }
                applyPowerUp(player, powerUp.getType());
                arenaLayer.getChildren().remove(powerUp.getView());
                iterator.remove();
                hud.showRoundMessage(player.getPlayerName() + " picked up " + powerUp.getType().getDisplayName());
                break;
            }
        }
    }

    /**
     * Spawns one pickup at a collision-free random position.
     */
    private void spawnPowerUp() {
        PowerUpType[] types = PowerUpType.values();
        PowerUpType type = types[random.nextInt(types.length)];
        for (int attempt = 0; attempt < 80; attempt++) {
            double x = 45 + random.nextDouble(mapData.getWidth() - PowerUp.SIZE - 90);
            double y = 45 + random.nextDouble(mapData.getHeight() - PowerUp.SIZE - 90);
            PowerUp candidate = new PowerUp(type, x, y);
            boolean blocked = obstacles.stream().anyMatch(candidate::intersects)
                    || players.stream().anyMatch(candidate::intersects)
                    || powerUps.stream().anyMatch(candidate::intersects);
            if (!blocked) {
                powerUps.add(candidate);
                arenaLayer.getChildren().add(candidate.render());
                return;
            }
        }
    }

    /**
     * Applies a pickup to a player.
     *
     * @param player collecting player
     * @param type pickup type
     */
    private void applyPowerUp(PlayerTank player, PowerUpType type) {
        switch (type) {
            case GATLING -> player.equipPowerUp(type, 28);
            case MINE -> player.equipPowerUp(type, 3);
            case LASER -> player.equipPowerUp(type, 2);
            case SHOTGUN -> player.equipPowerUp(type, 3);
            case SHIELD -> player.setShieldActive(true);
            case HOMING_MISSILE -> player.equipPowerUp(type, 2);
        }
    }

    /**
     * Applies damage, shield blocking, and possible round conclusion.
     *
     * @param target tank hit by a projectile or mine
     */
    private void hitTank(PlayerTank target, int damage) {
        boolean hadShield = target.hasShield();
        target.takeDamage(damage);
        playHitEffect(target);
        if (hadShield && !target.isDestroyed()) {
            hud.showRoundMessage(target.getPlayerName() + "'s shield blocked the hit");
            return;
        }
        if (target.isDestroyed()) {
            target.getView().setOpacity(0.25);
            playExplosion(target);
            concludeIfRoundResolved();
        }
    }

    /**
     * Ends the round if one or zero players remain alive.
     */
    private void concludeIfRoundResolved() {
        if (roundResetPending) {
            return;
        }
        List<PlayerTank> living = players.stream()
                .filter(player -> !player.isDestroyed())
                .toList();
        if (living.size() > 1) {
            return;
        }
        if (living.size() == 1) {
            PlayerTank winner = living.get(0);
            scoreManager.addPoint(players.indexOf(winner));
            concludeRound(winner.getPlayerName() + " scores");
        } else {
            concludeRound("Draw - everyone exploded");
        }
    }

    /**
     * Updates scores, clears input, and schedules the next state.
     *
     * @param message round result message
     */
    private void concludeRound(String message) {
        roundResetPending = true;
        inputHandler.clear();
        hud.showRoundMessage(message);
        updateHud();
        if (scoreManager.hasWinner()) {
            soundManager.playVictory();
            scheduleGameOver();
        } else {
            scheduleMapSwitch();
        }
    }

    /**
     * Schedules the next round on a random map after a short animation delay.
     */
    private void scheduleMapSwitch() {
        PauseTransition delay = new PauseTransition(Duration.seconds(ROUND_RESET_DELAY_SECONDS));
        delay.setOnFinished(event -> sceneManager.continueMatchOnRandomMap(scoreManager));
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
     * Resets tanks, bullets, pickups, and input state for the current map.
     */
    private void resetRound() {
        removeBullets();
        removeMines();
        removePowerUps();
        resetPlayer(0, mapData.getPlayerOneSpawn(), mapData.getPlayerOneRotation());
        resetPlayer(1, mapData.getPlayerTwoSpawn(), mapData.getPlayerTwoRotation());
        if (players.size() > 2) {
            resetPlayer(2, findOpenSpawn(), 270);
        }
        powerUpSpawnTimer = 2.0;
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
     * Resets one player to a spawn.
     *
     * @param playerIndex player slot
     * @param spawn spawn point
     * @param rotation spawn rotation
     */
    private void resetPlayer(int playerIndex, Point2D spawn, double rotation) {
        PlayerTank player = players.get(playerIndex);
        player.resetTo(spawn.getX(), spawn.getY(), rotation);
        player.clearPowerUp();
    }

    /**
     * Finds a safe spawn for the optional third player on maps built for two players.
     *
     * @return open spawn point
     */
    private Point2D findOpenSpawn() {
        List<Point2D> candidates = List.of(
                new Point2D(mapData.getWidth() / 2.0 - PlayerTank.TANK_WIDTH / 2.0,
                        mapData.getHeight() / 2.0 - PlayerTank.TANK_HEIGHT / 2.0),
                new Point2D(mapData.getWidth() / 2.0 - PlayerTank.TANK_WIDTH / 2.0, 72),
                new Point2D(mapData.getWidth() / 2.0 - PlayerTank.TANK_WIDTH / 2.0,
                        mapData.getHeight() - PlayerTank.TANK_HEIGHT - 72),
                new Point2D(72, mapData.getHeight() / 2.0 - PlayerTank.TANK_HEIGHT / 2.0),
                new Point2D(mapData.getWidth() - PlayerTank.TANK_WIDTH - 72,
                        mapData.getHeight() / 2.0 - PlayerTank.TANK_HEIGHT / 2.0));
        for (Point2D candidate : candidates) {
            if (isTankSpawnOpen(candidate)) {
                return candidate;
            }
        }
        return candidates.get(0);
    }

    /**
     * Tests whether a tank spawn is clear of walls and other tanks.
     *
     * @param spawn candidate left/top point
     * @return true when clear
     */
    private boolean isTankSpawnOpen(Point2D spawn) {
        Rectangle2D spawnBox = new Rectangle2D(
                spawn.getX() + PlayerTank.TANK_WIDTH * 0.18,
                spawn.getY() + PlayerTank.TANK_HEIGHT * 0.20,
                PlayerTank.TANK_WIDTH * 0.64,
                PlayerTank.TANK_HEIGHT * 0.60);
        boolean inside = collisionManager.isInsideArena(spawnBox, mapData);
        boolean hitsObstacle = obstacles.stream()
                .filter(Obstacle::blocksTanks)
                .anyMatch(obstacle -> obstacle.getCollisionBox().intersects(spawnBox));
        boolean hitsPlayer = players.stream()
                .map(PlayerTank::getCollisionBox)
                .anyMatch(box -> box.intersects(spawnBox));
        return inside && !hitsObstacle && !hitsPlayer;
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
     * Removes every mine from the arena.
     */
    private void removeMines() {
        mines.forEach(mine -> arenaLayer.getChildren().remove(mine.getView()));
        mines.clear();
    }

    /**
     * Removes every pickup from the arena.
     */
    private void removePowerUps() {
        powerUps.forEach(powerUp -> arenaLayer.getChildren().remove(powerUp.getView()));
        powerUps.clear();
    }

    /**
     * Finds the nearest living opponent for a homing missile.
     *
     * @param source firing tank
     * @return nearest target, or null when none are alive
     */
    private Tank findNearestOpponent(PlayerTank source) {
        PlayerTank nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (PlayerTank player : players) {
            if (player == source || player.isDestroyed()) {
                continue;
            }
            double distance = new Point2D(source.getCenterX(), source.getCenterY())
                    .distance(player.getCenterX(), player.getCenterY());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = player;
            }
        }
        return nearest;
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
     * Refreshes the HUD labels.
     */
    private void updateHud() {
        hud.update(
                scoreManager,
                players.get(0),
                players.get(1),
                players.size() > 2 ? players.get(2) : null);
    }

    /**
     * Calculates distance between two arena points.
     *
     * @param x1 first x
     * @param y1 first y
     * @param x2 second x
     * @param y2 second y
     * @return Euclidean distance
     */
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    /**
     * A rendered or damaging laser segment.
     *
     * @param startX start x
     * @param startY start y
     * @param endX end x
     * @param endY end y
     */
    private record LineSegment(double startX, double startY, double endX, double endY) {
    }

    /**
     * Where a laser ray hit and how it should reflect.
     *
     * @param x impact x
     * @param y impact y
     * @param axis reflection axis
     */
    private record LaserImpact(double x, double y, BounceAxis axis) {
    }

    /**
     * Reflection axis for laser rays.
     */
    private enum BounceAxis {
        NONE,
        HORIZONTAL,
        VERTICAL,
        BOTH
    }
}
