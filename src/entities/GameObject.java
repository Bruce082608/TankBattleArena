package entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Abstract base class for every visible object placed in the arena.
 */
public abstract class GameObject {
    private double x;
    private double y;
    private final double width;
    private final double height;
    private final ImageView view;

    /**
     * Builds a game object with a position, size, and sprite image.
     *
     * @param x left coordinate in arena space
     * @param y top coordinate in arena space
     * @param width rendered width
     * @param height rendered height
     * @param image sprite image used by the object's ImageView
     */
    protected GameObject(double x, double y, double width, double height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.view = new ImageView(image);
        this.view.setFitWidth(width);
        this.view.setFitHeight(height);
        this.view.setPreserveRatio(false);
        render();
    }

    /**
     * Updates the object state for one game-loop tick.
     *
     * @param deltaSeconds elapsed time since the previous frame
     */
    public abstract void update(double deltaSeconds);

    /**
     * Synchronizes the JavaFX ImageView with the model coordinates.
     *
     * @return ImageView that represents the object on the scene graph
     */
    public ImageView render() {
        view.setLayoutX(x);
        view.setLayoutY(y);
        return view;
    }

    /**
     * Moves the object to an absolute arena coordinate.
     *
     * @param x new left coordinate
     * @param y new top coordinate
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        render();
    }

    /**
     * Returns the current collision rectangle.
     *
     * @return rectangle used for collision checks
     */
    public Rectangle2D getCollisionBox() {
        return getProjectedCollisionBox(x, y);
    }

    /**
     * Returns a collision rectangle at a proposed position.
     *
     * @param proposedX proposed left coordinate
     * @param proposedY proposed top coordinate
     * @return rectangle for the proposed position
     */
    public Rectangle2D getProjectedCollisionBox(double proposedX, double proposedY) {
        return new Rectangle2D(proposedX, proposedY, width, height);
    }

    /**
     * Tests whether this object intersects another object.
     *
     * @param other other object to test against
     * @return true when the collision boxes overlap
     */
    public boolean intersects(GameObject other) {
        return getCollisionBox().intersects(other.getCollisionBox());
    }

    /**
     * Gets the JavaFX ImageView for this object.
     *
     * @return object view
     */
    public ImageView getView() {
        return view;
    }

    /**
     * Gets the object's left coordinate.
     *
     * @return left coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the object's top coordinate.
     *
     * @return top coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the rendered width.
     *
     * @return width in pixels
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets the rendered height.
     *
     * @return height in pixels
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets the horizontal center coordinate.
     *
     * @return center x coordinate
     */
    public double getCenterX() {
        return x + width / 2.0;
    }

    /**
     * Gets the vertical center coordinate.
     *
     * @return center y coordinate
     */
    public double getCenterY() {
        return y + height / 2.0;
    }
}
