package systems;

/**
 * Tracks player scores across rounds in a match.
 */
public class ScoreManager {
    private final int scoreToWin;
    private final int[] scores;

    /**
     * Creates a score manager for a match.
     *
     * @param scoreToWin score required to reach the game-over screen
     */
    public ScoreManager(int scoreToWin) {
        this(scoreToWin, 2);
    }

    /**
     * Creates a score manager for a match.
     *
     * @param scoreToWin score required to reach the game-over screen
     * @param playerCount number of local players
     */
    public ScoreManager(int scoreToWin, int playerCount) {
        this.scoreToWin = scoreToWin;
        this.scores = new int[playerCount];
    }

    /**
     * Adds one point to player one.
     */
    public void addPlayerOnePoint() {
        addPoint(0);
    }

    /**
     * Adds one point to player two.
     */
    public void addPlayerTwoPoint() {
        addPoint(1);
    }

    /**
     * Adds one point to a zero-based player slot.
     *
     * @param playerIndex player slot
     */
    public void addPoint(int playerIndex) {
        scores[playerIndex]++;
    }

    /**
     * Gets player one's score.
     *
     * @return player one score
     */
    public int getPlayerOneScore() {
        return scores[0];
    }

    /**
     * Gets player two's score.
     *
     * @return player two score
     */
    public int getPlayerTwoScore() {
        return scores.length > 1 ? scores[1] : 0;
    }

    /**
     * Gets a score by player slot.
     *
     * @param playerIndex zero-based player slot
     * @return player score
     */
    public int getScore(int playerIndex) {
        return scores[playerIndex];
    }

    /**
     * Gets the number of tracked players.
     *
     * @return player count
     */
    public int getPlayerCount() {
        return scores.length;
    }

    /**
     * Gets the winning score threshold.
     *
     * @return score required to win the match
     */
    public int getScoreToWin() {
        return scoreToWin;
    }

    /**
     * Reports whether either player has won the match.
     *
     * @return true when a player reached the winning score
     */
    public boolean hasWinner() {
        for (int score : scores) {
            if (score >= scoreToWin) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the current match winner name.
     *
     * @return winner label, or "No winner" when nobody has won
     */
    public String getWinnerName() {
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] >= scoreToWin) {
                return "Player " + (i + 1);
            }
        }
        return "No winner";
    }

    /**
     * Formats all player scores in order.
     *
     * @return score summary
     */
    public String formatScores() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < scores.length; i++) {
            if (i > 0) {
                builder.append(" : ");
            }
            builder.append(scores[i]);
        }
        return builder.toString();
    }
}
