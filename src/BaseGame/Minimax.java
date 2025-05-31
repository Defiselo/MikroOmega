package BaseGame;

import java.util.List;

/**
 * Implements the BaseGame.Minimax algorithm with Alpha-Beta Pruning.
 * This class provides a generic AI solution for any game that implements the `BaseGame.Game` interface.
 */
public class Minimax {

    /**
     * Finds the best move for the AI player using the BaseGame.Minimax algorithm.
     *
     * @param game The current state of the game.
     * @param depth The maximum depth to search in the game tree. Higher depth means better AI, but slower.
     * @param maximizingPlayer The player for whom we are trying to maximize the score (usually AI).
     * @return The `BaseGame.Move` object representing the best move found for the AI.
     */
    public static Move findBestMove(Game game, int depth, Player maximizingPlayer) {
        int bestVal = Integer.MIN_VALUE; // Initialize best value to negative infinity
        Move bestMove = null; // Initialize best move to null

        // Get all valid moves from the current game state
        List<Move> validMoves = game.getValidMoves();

        // If no valid moves, return null (shouldn't happen if game is not over)
        if (validMoves.isEmpty()) {
            return null;
        }

        // Iterate through all possible valid moves
        for (Move move : validMoves) {
            // Create a deep copy of the game state to explore the move hypothetically
            Game newGame = game.clone();

            // Make the move on the cloned game state for the maximizing player
            newGame.makeMove(move, maximizingPlayer);

            // Recursively call minimax for the next player (minimizing player)
            // Initial alpha is MIN_VALUE, initial beta is MAX_VALUE
            int moveVal = minimax(newGame, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
                    (maximizingPlayer == Player.HUMAN) ? Player.AI : Player.HUMAN, // Next player is minimizing
                    maximizingPlayer); // The player whose turn it *was* for evaluation

            // If the value of the current move is better than the best value found so far,
            // update bestVal and bestMove.
            if (moveVal > bestVal) {
                bestVal = moveVal;
                bestMove = move;
            }
        }
        return bestMove; // Return the best move found
    }

    /**
     * The recursive Minimax function with Alpha-Beta Pruning.
     *
     * @param game The current game state.
     * @param depth The remaining depth to search.
     * @param alpha The alpha value (best score for maximizing player found so far).
     * @param beta The beta value (best score for minimizing player found so far).
     * @param currentPlayer The player whose turn it is in the current recursive call.
     * @param maximizingPlayer The player for whom we are trying to maximize the score (AI).
     * @return The evaluated score for the current game state.
     */
    private static int minimax(Game game, int depth, int alpha, int beta, Player currentPlayer, Player maximizingPlayer) {
        if (depth == 0 || game.isGameOver()) {
            return game.evaluate();
        }

        // Check if the current player is the maximizing player (AI)
        if (currentPlayer == maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE; // Initialize maxEval to negative infinity

            // Iterate through all valid moves for the current player
            for (Move move : game.getValidMoves()) {
                // Create a deep copy of the game to explore the move
                Game newGame = game.clone();
                newGame.makeMove(move, currentPlayer); // Make the move

                // Recursively call minimax for the next player (minimizing player)
                int eval = minimax(newGame, depth - 1, alpha, beta,
                        (currentPlayer == Player.HUMAN) ? Player.AI : Player.HUMAN, // Toggle player
                        maximizingPlayer);

                maxEval = Math.max(maxEval, eval); // Update maxEval

                alpha = Math.max(alpha, eval); // Update alpha
                if (beta <= alpha) {
                    break; // Alpha-beta pruning: Beta cut-off
                }
            }
            return maxEval; // Return the maximum evaluation for the maximizing player
        } else { // Current player is the minimizing player (Human)
            int minEval = Integer.MAX_VALUE; // Initialize minEval to positive infinity

            // Iterate through all valid moves for the current player
            for (Move move : game.getValidMoves()) {
                // Create a deep copy of the game to explore the move
                Game newGame = game.clone();
                newGame.makeMove(move, currentPlayer); // Make the move

                // Recursively call minimax for the next player (maximizing player)
                int eval = minimax(newGame, depth - 1, alpha, beta,
                        (currentPlayer == Player.HUMAN) ? Player.AI : Player.HUMAN, // Toggle player
                        maximizingPlayer);

                minEval = Math.min(minEval, eval); // Update minEval

                beta = Math.min(beta, eval); // Update beta
                if (beta <= alpha) {
                    break; // Alpha-beta pruning: Alpha cut-off
                }
            }
            return minEval; // Return the minimum evaluation for the minimizing player
        }
    }
}

