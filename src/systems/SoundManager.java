package systems;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Optional sound facade that quietly disables missing audio files.
 */
public class SoundManager {
    private static final String SHOOT_FILE = "shoot.mp3";
    private static final String EXPLOSION_FILE = "explosion.mp3";
    private static final String VICTORY_FILE = "victory.mp3";

    /**
     * Plays the shooting sound if a matching resource exists.
     */
    public void playShoot() {
        play(SHOOT_FILE);
    }

    /**
     * Plays the explosion sound if a matching resource exists.
     */
    public void playExplosion() {
        play(EXPLOSION_FILE);
    }

    /**
     * Plays the victory sound if a matching resource exists.
     */
    public void playVictory() {
        play(VICTORY_FILE);
    }

    /**
     * Attempts to load and play a sound by filename.
     *
     * @param fileName audio filename under resources/sounds or classpath /sounds
     */
    private void play(String fileName) {
        String source = findSoundSource(fileName);
        if (source == null) {
            return;
        }
        try {
            MediaPlayer player = new MediaPlayer(new Media(source));
            player.setOnEndOfMedia(player::dispose);
            player.play();
        } catch (RuntimeException ignored) {
            // Media support is optional for this project.
        }
    }

    /**
     * Finds a sound as either a classpath resource or a project file.
     *
     * @param fileName audio filename
     * @return external media URI, or null when absent
     */
    private String findSoundSource(String fileName) {
        URL classpathResource = getClass().getResource("/sounds/" + fileName);
        if (classpathResource != null) {
            return classpathResource.toExternalForm();
        }
        Path projectResource = Path.of("resources", "sounds", fileName);
        if (Files.exists(projectResource)) {
            return projectResource.toUri().toString();
        }
        return null;
    }
}
