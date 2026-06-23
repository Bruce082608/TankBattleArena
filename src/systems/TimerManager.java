package systems;

/**
 * Manages the countdown timer for each round.
 */
public class TimerManager {
    private final double roundLengthSeconds;
    private double remainingSeconds;

    /**
     * Creates a timer with a fixed round length.
     *
     * @param roundLengthSeconds length of each round in seconds
     */
    public TimerManager(double roundLengthSeconds) {
        this.roundLengthSeconds = roundLengthSeconds;
        reset();
    }

    /**
     * Advances the countdown.
     *
     * @param deltaSeconds elapsed time since the previous frame
     */
    public void update(double deltaSeconds) {
        remainingSeconds = Math.max(0, remainingSeconds - deltaSeconds);
    }

    /**
     * Resets the timer back to the full round length.
     */
    public void reset() {
        remainingSeconds = roundLengthSeconds;
    }

    /**
     * Reports whether the round timer has expired.
     *
     * @return true when no time remains
     */
    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

    /**
     * Formats the remaining time as M:SS.
     *
     * @return formatted time label
     */
    public String formatTime() {
        int totalSeconds = (int) Math.ceil(remainingSeconds);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Gets the remaining seconds.
     *
     * @return seconds left in the round
     */
    public double getRemainingSeconds() {
        return remainingSeconds;
    }
}
