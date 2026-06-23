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
    }
}
