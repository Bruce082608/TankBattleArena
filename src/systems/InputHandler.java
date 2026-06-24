package systems;

import java.util.EnumSet;
import java.util.Set;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * Tracks pressed keys and dispatches global gameplay key commands.
 */
public class InputHandler {
    private final Set<KeyCode> pressedKeys = EnumSet.noneOf(KeyCode.class);
    private double mouseX;
    private double mouseY;
    private boolean primaryMousePressed;
    private boolean mousePositionKnown;

    /**
     * Attaches keyboard listeners to a JavaFX scene.
     *
     * @param scene scene that should receive key events
     * @param escapeAction action invoked when ESC is pressed
     */
    public void attachToScene(Scene scene, Runnable escapeAction) {
        scene.setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
            if (event.getCode() == KeyCode.ESCAPE) {
                escapeAction.run();
                event.consume();
            }
        });
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
        scene.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            mousePositionKnown = true;
        });
        scene.setOnMouseDragged(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            mousePositionKnown = true;
        });
        scene.setOnMousePressed(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            mousePositionKnown = true;
            primaryMousePressed = event.isPrimaryButtonDown();
        });
        scene.setOnMouseReleased(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            mousePositionKnown = true;
            primaryMousePressed = event.isPrimaryButtonDown();
        });
    }

    /**
     * Tests whether a key is currently pressed.
     *
     * @param key key to query
     * @return true when the key is held down
     */
    public boolean isPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }

    /**
     * Clears all pressed-key state, useful after pausing or resetting.
     */
    public void clear() {
        pressedKeys.clear();
        primaryMousePressed = false;
    }

    /**
     * Gets the last scene-space mouse x coordinate.
     *
     * @return mouse x
     */
    public double getMouseX() {
        return mouseX;
    }

    /**
     * Gets the last scene-space mouse y coordinate.
     *
     * @return mouse y
     */
    public double getMouseY() {
        return mouseY;
    }

    /**
     * Reports whether the primary mouse button is held.
     *
     * @return true when pressed
     */
    public boolean isPrimaryMousePressed() {
        return primaryMousePressed;
    }

    /**
     * Reports whether a real mouse coordinate has been received.
     *
     * @return true after the first mouse event
     */
    public boolean hasMousePosition() {
        return mousePositionKnown;
    }
}
