package BaseGame;

import java.util.List;

/**
 * An interface representing a generic game that can be played by a human or an AI.
 * This interface provides the necessary methods for the BaseGame.Minimax algorithm to interact
 * with any game, allowing for a generic AI implementation.
 */
public interface Game {

    /**
     * Makes a move on the game board for the specified player.
     * This method updates the internal state of the game based on the move.
     *
     * @param move The `BaseGame.Move` object representing the action to be taken.
     * @param player The `BaseGame.Player` making the move.
     */
    void makeMove(Move move, Player player);

    /**
     * Undoes the last move made on the game board.
     * This is crucial for the BaseGame.Minimax algorithm to explore different game states
     * without permanently altering the original board.
     *
     * @param move The `BaseGame.Move` object that was previously made and needs to be undone.
     */
    void undoMove(Move move);

    /**
     * Checks if the game is over (either a win, loss, or draw).
     *
     * @return `true` if the game has ended, `false` otherwise.
     */
    boolean isGameOver();

    /**
     * Determines the winner of the game.
     *
     * @return The `BaseGame.Player` who won the game, or `BaseGame.Player.NONE` if it's a draw or the game is not yet over.
     */
    Player getWinner();

    /**
     * Gets a list of all valid moves that can be made from the current game state.
     *
     * @return A `List` of `BaseGame.Move` objects representing all possible valid moves.
     */
    List<Move> getValidMoves();

    /**
     * Evaluates the current state of the game board from the perspective of the AI player.
     * This function assigns a numerical score to the board, indicating how favorable it is
     * for the AI.
     *
     * @return An integer score representing the evaluation of the current board state.
     * Positive values indicate a favorable state for the AI, negative for the opponent,
     * and zero for a neutral or draw state.
     */
    int evaluate();

    /**
     * Creates a deep copy of the current game state.
     * This is essential for the BaseGame.Minimax algorithm to explore hypothetical moves
     * without modifying the actual game board.
     *
     * @return A new `BaseGame.Game` object that is an independent copy of the current game state.
     */
    Game clone();

    /**
     * Gets the player whose turn it is currently.
     *
     * @return The `BaseGame.Player` whose turn it is.
     */
    Player getCurrentPlayer();

    /**
     * Sets the current player.
     *
     * @param player The `BaseGame.Player` whose turn it is.
     */
    void setCurrentPlayer(Player player);
}
