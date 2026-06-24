package ui;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import systems.PowerUpType;

/**
 * Generates bitmap sprites used by ImageView-based game objects.
 */
public final class GameArt {
    private static final int SPRITE_SIZE = 96;

    /**
     * Prevents construction of this utility class.
     */
    private GameArt() {
    }

    /**
     * Creates a tank sprite facing to the right.
     *
     * @param body main tank color
     * @param accent accent color
     * @return generated tank image
     */
    public static Image createTankImage(Color body, Color accent) {
        Canvas canvas = new Canvas(SPRITE_SIZE, SPRITE_SIZE);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.TRANSPARENT);
        graphics.fillRect(0, 0, SPRITE_SIZE, SPRITE_SIZE);
        graphics.setFill(body.darker());
        graphics.fillRoundRect(14, 24, 64, 48, 14, 14);
        graphics.setFill(body);
        graphics.fillRoundRect(22, 16, 46, 64, 16, 16);
        graphics.setFill(accent);
        graphics.fillRoundRect(42, 34, 46, 12, 6, 6);
        graphics.setFill(Color.rgb(30, 33, 38, 0.9));
        graphics.fillOval(34, 29, 28, 28);
        graphics.setStroke(Color.rgb(255, 255, 255, 0.35));
        graphics.setLineWidth(3);
        graphics.strokeRoundRect(22, 16, 46, 64, 16, 16);
        return snapshot(canvas);
    }

    /**
     * Creates a bullet sprite.
     *
     * @param color bullet color
     * @return generated bullet image
     */
    public static Image createBulletImage(Color color) {
        Canvas canvas = new Canvas(32, 32);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.TRANSPARENT);
        graphics.fillRect(0, 0, 32, 32);
        graphics.setFill(color);
        graphics.fillOval(6, 6, 20, 20);
        graphics.setStroke(Color.WHITE);
        graphics.setLineWidth(2);
        graphics.strokeOval(7, 7, 18, 18);
        return snapshot(canvas);
    }

    /**
     * Creates a compact pickup icon.
     *
     * @param type pickup type to draw
     * @return generated powerup image
     */
    public static Image createPowerUpImage(PowerUpType type) {
        Canvas canvas = new Canvas(64, 64);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.TRANSPARENT);
        graphics.fillRect(0, 0, 64, 64);
        graphics.setFill(Color.rgb(28, 32, 36, 0.94));
        graphics.fillRoundRect(8, 8, 48, 48, 10, 10);
        graphics.setStroke(type.getColor());
        graphics.setLineWidth(5);
        graphics.strokeRoundRect(10, 10, 44, 44, 8, 8);
        graphics.setFill(type.getColor());
        graphics.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 25));
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.fillText(type.getSymbol(), 32, 41);
        return snapshot(canvas);
    }

    /**
     * Creates a mine sprite.
     *
     * @param color mine accent color
     * @return generated mine image
     */
    public static Image createMineImage(Color color) {
        Canvas canvas = new Canvas(48, 48);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.TRANSPARENT);
        graphics.fillRect(0, 0, 48, 48);
        graphics.setFill(Color.rgb(20, 22, 24, 0.95));
        graphics.fillOval(9, 9, 30, 30);
        graphics.setStroke(color);
        graphics.setLineWidth(4);
        graphics.strokeOval(11, 11, 26, 26);
        graphics.setFill(color);
        graphics.fillOval(20, 20, 8, 8);
        return snapshot(canvas);
    }

    /**
     * Creates a brick wall texture.
     *
     * @return generated brick image
     */
    public static Image createBrickWallImage() {
        Canvas canvas = new Canvas(SPRITE_SIZE, SPRITE_SIZE);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.rgb(126, 57, 48));
        graphics.fillRect(0, 0, SPRITE_SIZE, SPRITE_SIZE);
        graphics.setStroke(Color.rgb(75, 34, 31));
        graphics.setLineWidth(4);
        for (int y = 16; y < SPRITE_SIZE; y += 24) {
            graphics.strokeLine(0, y, SPRITE_SIZE, y);
        }
        for (int y = 0; y < SPRITE_SIZE; y += 24) {
            int offset = (y / 24) % 2 == 0 ? 0 : 24;
            for (int x = -offset; x < SPRITE_SIZE; x += 48) {
                graphics.strokeLine(x, y, x, y + 24);
            }
        }
        return snapshot(canvas);
    }

    /**
     * Creates a stone wall texture.
     *
     * @return generated stone image
     */
    public static Image createStoneWallImage() {
        Canvas canvas = new Canvas(SPRITE_SIZE, SPRITE_SIZE);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.rgb(109, 120, 126));
        graphics.fillRect(0, 0, SPRITE_SIZE, SPRITE_SIZE);
        graphics.setStroke(Color.rgb(62, 69, 74));
        graphics.setLineWidth(3);
        graphics.strokeRoundRect(7, 7, 82, 82, 10, 10);
        graphics.strokeLine(18, 30, 82, 30);
        graphics.strokeLine(26, 56, 88, 56);
        graphics.strokeLine(38, 10, 38, 56);
        graphics.strokeLine(64, 30, 64, 88);
        return snapshot(canvas);
    }

    /**
     * Creates a tree texture.
     *
     * @return generated tree image
     */
    public static Image createTreeImage() {
        Canvas canvas = new Canvas(SPRITE_SIZE, SPRITE_SIZE);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.rgb(88, 54, 28));
        graphics.fillRoundRect(40, 52, 16, 38, 8, 8);
        graphics.setFill(Color.rgb(37, 113, 68));
        graphics.fillOval(18, 18, 44, 44);
        graphics.setFill(Color.rgb(51, 143, 80));
        graphics.fillOval(36, 8, 44, 44);
        graphics.setFill(Color.rgb(26, 95, 61));
        graphics.fillOval(28, 34, 48, 42);
        return snapshot(canvas);
    }

    /**
     * Creates a wooden barricade texture.
     *
     * @return generated wood image
     */
    public static Image createWoodImage() {
        Canvas canvas = new Canvas(SPRITE_SIZE, SPRITE_SIZE);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.rgb(130, 82, 43));
        graphics.fillRect(0, 0, SPRITE_SIZE, SPRITE_SIZE);
        graphics.setStroke(Color.rgb(82, 48, 24));
        graphics.setLineWidth(5);
        graphics.strokeLine(0, 24, SPRITE_SIZE, 24);
        graphics.strokeLine(0, 50, SPRITE_SIZE, 50);
        graphics.strokeLine(0, 76, SPRITE_SIZE, 76);
        graphics.setStroke(Color.rgb(190, 132, 72, 0.7));
        graphics.setLineWidth(2);
        graphics.strokeLine(10, 8, 82, 13);
        graphics.strokeLine(18, 38, 88, 34);
        graphics.strokeLine(4, 66, 80, 70);
        return snapshot(canvas);
    }

    /**
     * Creates a sand barrier texture.
     *
     * @return generated sand image
     */
    public static Image createSandImage() {
        Canvas canvas = new Canvas(SPRITE_SIZE, SPRITE_SIZE);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.rgb(202, 178, 110));
        graphics.fillRect(0, 0, SPRITE_SIZE, SPRITE_SIZE);
        graphics.setFill(Color.rgb(174, 145, 82));
        for (int i = 0; i < 7; i++) {
            graphics.fillOval(8 + i * 13, 18 + (i % 3) * 18, 10, 6);
        }
        graphics.setStroke(Color.rgb(118, 93, 55, 0.6));
        graphics.setLineWidth(4);
        graphics.strokeRoundRect(5, 5, 86, 86, 20, 20);
        return snapshot(canvas);
    }

    /**
     * Creates an explosion effect image.
     *
     * @return generated explosion image
     */
    public static Image createExplosionImage() {
        Canvas canvas = new Canvas(140, 140);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.TRANSPARENT);
        graphics.fillRect(0, 0, 140, 140);
        graphics.setFill(Color.rgb(255, 211, 64, 0.95));
        graphics.fillOval(25, 25, 90, 90);
        graphics.setFill(Color.rgb(255, 95, 36, 0.88));
        graphics.fillOval(10, 42, 120, 56);
        graphics.fillOval(42, 10, 56, 120);
        graphics.setFill(Color.rgb(80, 45, 42, 0.55));
        graphics.fillOval(42, 42, 56, 56);
        return snapshot(canvas);
    }

    /**
     * Creates a compact impact spark image.
     *
     * @return generated hit image
     */
    public static Image createHitImage() {
        Canvas canvas = new Canvas(48, 48);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.TRANSPARENT);
        graphics.fillRect(0, 0, 48, 48);
        graphics.setFill(Color.rgb(255, 225, 88, 0.95));
        graphics.fillOval(10, 10, 28, 28);
        graphics.setStroke(Color.rgb(255, 113, 45, 0.9));
        graphics.setLineWidth(4);
        graphics.strokeLine(24, 2, 24, 46);
        graphics.strokeLine(2, 24, 46, 24);
        return snapshot(canvas);
    }

    /**
     * Captures a canvas as a transparent image.
     *
     * @param canvas source canvas
     * @return writable image snapshot
     */
    private static Image snapshot(Canvas canvas) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        return canvas.snapshot(parameters, image);
    }
}
