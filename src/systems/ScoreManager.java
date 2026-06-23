package systems;

/**
 * Tracks player scores across rounds in a match.
 */
public class ScoreManager {
    private final int scoreToWin;
    private int playerOneScore;
    private int playerTwoScore;

    /**
     * Creates a score manager for a match.
     *
     * @param scoreToWin score required to reach the game-over screen
     */
    public ScoreManager(int scoreToWin) {
        this.scoreToWin = scoreToWin;
    }

    /**
     * Adds one point to player one.
     */
    public void addPlayerOnePoint() {
        playerOneScore++;
    }

    /**
     * Adds one point to player two.
     */
    public void addPlayerTwoPoint() {
        playerTwoScore++;
    }

    /**
     * Gets player one's score.
     *
     * @return player one score
     */
    public int getPlayerOneScore() {
        return playerOneScore;
    }

    /**
     * Gets player two's score.
     *
     * @return player two score
     */
    public int getPlayerTwoScore() {
        return playerTwoScore;
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
        return playerOneScore >= scoreToWin || playerTwoScore >= scoreToWin;
    }

    /**
     * Gets the current match winner name.
     *
     * @return winner label, or "No winner" when nobody has won
     */
    public String getWinnerName() {
        if (playerOneScore >= scoreToWin) {
            return "Player 1";
        }
        if (playerTwoScore >= scoreToWin) {
            return "Player 2";
        }
        return "No winner";
    }
}
