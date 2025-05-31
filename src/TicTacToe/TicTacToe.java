package TicTacToe;

import BaseGame.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game logic and state for Tic-Tac-Toe.
 * Implements the `BaseGame.Game` interface to be compatible with the generic BaseGame.Minimax AI.
 */
public class TicTacToe implements Game {
    public static final int BOARD_SIZE = 3; // Tic-Tac-Toe board is 3x3
    private Player[][] board; // The game board
    private Player currentPlayer; // The player whose turn it is
    private Move lastMove; // Stores the last move made for undo functionality

    /**
     * Constructs a new TicTacToe instance.
     * Initializes the board with empty cells and sets the starting player to HUMAN.
     */
    public TicTacToe() {
        board = new Player[BOARD_SIZE][BOARD_SIZE];
        // Initialize all cells to BaseGame.Player.NONE (empty)
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = Player.NONE;
            }
        }
            currentPlayer = Player.HUMAN;

    }

    /**
     * Private constructor for cloning the game state.
     *
     * @param board The board state to copy.
     * @param currentPlayer The current player to set.
     * @param lastMove The last move to set.
     */
    private TicTacToe(Player[][] board, Player currentPlayer, Move lastMove) {
        this.board = new Player[BOARD_SIZE][BOARD_SIZE];
        // Deep copy the board
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, BOARD_SIZE);
        }
        this.currentPlayer = currentPlayer;
        this.lastMove = lastMove;
    }

    /**
     * Makes a move on the Tic-Tac-Toe board.
     *
     * @param move The `BaseGame.Move` object (row, col) where the player wants to place their mark.
     * @param player The `BaseGame.Player` (HUMAN or AI) making the move.
     */
    @Override
    public void makeMove(Move move, Player player) {
        if (isValidMove(move)) {
            board[move.row()][move.col()] = player;
            lastMove = move; // Store the move for potential undo
            // Toggle current player after a move
            currentPlayer = (player == Player.HUMAN) ? Player.AI : Player.HUMAN;
        } else {
            System.err.println("Invalid move attempted at (" + move.row() + ", " + move.col() + ")");
        }
    }

    /**
     * Undoes the last move made on the board.
     * This is primarily used by the BaseGame.Minimax algorithm for exploring game states.
     *
     * @param move The `BaseGame.Move` object that was previously made and needs to be undone.
     */
    @Override
    public void undoMove(Move move) {
        if (move != null) {
            board[move.row()][move.col()] = Player.NONE; // Clear the cell
            // Toggle current player back
            currentPlayer = (currentPlayer == Player.HUMAN) ? Player.AI : Player.HUMAN;
            lastMove = null; // Clear last move after undo
        }
    }

    /**
     * Checks if a given move is valid (i.e., within board boundaries and the cell is empty).
     *
     * @param move The `BaseGame.Move` object to check.
     * @return `true` if the move is valid, `false` otherwise.
     */
    public boolean isValidMove(Move move) {
        return move.row() >= 0 && move.row() < BOARD_SIZE &&
                move.col() >= 0 && move.col() < BOARD_SIZE &&
                board[move.row()][move.col()] == Player.NONE;
    }

    /**
     * Checks if the game is over. A game is over if there's a winner or if the board is full (draw).
     *
     * @return `true` if the game has ended, `false` otherwise.
     */
    @Override
    public boolean isGameOver() {
        return getWinner() != Player.NONE || getValidMoves().isEmpty();
    }

    /**
     * Determines the winner of the Tic-Tac-Toe game.
     * Checks all rows, columns, and diagonals for a winning line.
     *
     * @return The `BaseGame.Player` who won (HUMAN or AI), or `BaseGame.Player.NONE` if no winner yet or it's a draw.
     */
    @Override
    public Player getWinner() {
        // Check rows
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] != Player.NONE && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0];
            }
        }

        // Check columns
        for (int j = 0; j < BOARD_SIZE; j++) {
            if (board[0][j] != Player.NONE && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j];
            }
        }

        // Check diagonals
        // Top-left to bottom-right
        if (board[0][0] != Player.NONE && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0];
        }
        // Top-right to bottom-left
        if (board[0][2] != Player.NONE && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2];
        }

        return Player.NONE; // No winner yet
    }

    /**
     * Gets a list of all empty cells on the board, which represent valid moves.
     *
     * @return A `List` of `BaseGame.Move` objects for all valid moves.
     */
    @Override
    public List<Move> getValidMoves() {
        List<Move> validMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == Player.NONE) {
                    validMoves.add(new Move(i, j));
                }
            }
        }
        return validMoves;
    }

    /**
     * Evaluates the current state of the Tic-Tac-Toe board for the AI player.
     * This function is crucial for the BaseGame.Minimax algorithm.
     * - Returns a high positive score if AI wins.
     * - Returns a high negative score if Human wins.
     * - Returns 0 for a draw or if the game is still ongoing.
     *
     * @return An integer score representing the evaluation of the current board state.
     */
    @Override
    public int evaluate() {
        Player winner = getWinner();
        if (winner == Player.AI) {
            return 10; // AI wins
        } else if (winner == Player.HUMAN) {
            return -10; // Human wins
        }
        return 0; // Draw or game still in progress
    }

    /**
     * Creates a deep copy of the current Tic-Tac-Toe game state.
     * This is vital for the BaseGame.Minimax algorithm to explore hypothetical moves
     * without modifying the actual game board.
     *
     * @return A new `TicTacToe` object that is an independent copy.
     */
    @Override
    public Game clone() {
        return new TicTacToe(this.board, this.currentPlayer, this.lastMove);
    }

    /**
     * Gets the current player whose turn it is.
     *
     * @return The `BaseGame.Player` whose turn it is.
     */
    @Override
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the current player.
     *
     * @param player The `BaseGame.Player` to set as the current player.
     */
    @Override
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    /**
     * Gets the player at a specific cell on the board.
     *
     * @param row The row index.
     * @param col The column index.
     * @return The `BaseGame.Player` occupying that cell, or `BaseGame.Player.NONE` if empty.
     */
    public Player getCell(int row, int col) {
        return board[row][col];
    }
}

