package Connect4;

import BaseGame.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Represents the game logic and state for Connect4.
 * Implements the `BaseGame.Game` interface to be compatible with the generic BaseGame.Minimax AI.
 */
public class Connect4 implements Game {
    public static final int ROWS = 6; // Number of rows in Connect4 board
    public static final int COLS = 7;// Number of columns in Connect4 board
    public static final int WIN_CON_LENGTH = 4;
    private Player[][] board; // The game board
    private Player currentPlayer; // The player whose turn it is
    private List<Move> moveHistory; // Stores moves for undo functionality (important for AI)

    /**
     * Constructs a new Connect4 instance.
     * Initializes the board with empty cells and sets the starting player to HUMAN.
     */
    public Connect4() {
        board = new Player[ROWS][COLS];
        // Initialize all cells to BaseGame.Player.NONE (empty)
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = Player.NONE;
            }
        }
        currentPlayer = Player.HUMAN; // Human starts the game
        moveHistory = new ArrayList<>(); // Initialize move history
    }

    /**
     * Private constructor for cloning the game state.
     * Performs a deep copy of the board and move history.
     *
     * @param board The board state to copy.
     * @param currentPlayer The current player to set.
     * @param moveHistory The move history to copy.
     */
    private Connect4(Player[][] board, Player currentPlayer, List<Move> moveHistory) {
        this.board = new Player[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, COLS);
        }
        this.currentPlayer = currentPlayer;
        this.moveHistory = new ArrayList<>(moveHistory);
    }

    /**
     * Makes a move on the Connect4 board.
     * In Connect4, moves are made by dropping a piece into a column. The piece falls to the lowest available row.
     *
     * @param moveData The `BaseGame.Move` object (only `col` is relevant for Connect4, `row` is determined by gravity).
     * @param playerMakingMove The `BaseGame.Player` (HUMAN or AI) making the move.
     */
    @Override
    public void makeMove(Move moveData, Player playerMakingMove) {
        int targetCol = moveData.col();
        if (!isColumnValidForDrop(targetCol)) {
            throw new IllegalArgumentException("Invalid column index: " + targetCol + ". Column is full or out of bounds.");
        }

        int finalRow = findNextAvailableRow(targetCol);
        if (finalRow == -1) {
            throw new IllegalArgumentException("Column " + targetCol + " is already completely full.");
        }

        board[finalRow][targetCol] = playerMakingMove;
        moveHistory.add(new Move(finalRow, targetCol));
        currentPlayer = getOpponent(playerMakingMove);
    }



    /**
     * Undoes the last move made on the board.
     * This is primarily used by the BaseGame.Minimax algorithm for exploring game states.
     *
     * @param moveData The `BaseGame.Move` object that was previously made and needs to be undone.
     * For Connect4, this should be the exact `BaseGame.Move` (row, col) where the piece landed.
     */
    @Override
    public void undoMove(Move moveData) {
        if (moveHistory.isEmpty() || !moveHistory.get(moveHistory.size() - 1).equals(moveData)) {
            throw new IllegalStateException("Attempt to undo move failed: history mismatch.");
        }

        board[moveData.row()][moveData.col()] = Player.NONE;
        moveHistory.remove(moveHistory.size() - 1);
        currentPlayer = getOpponent(currentPlayer);
    }

    public int findNextAvailableRow(int col) {
        for (int r = ROWS - 1; r >= 0; r--) {
            if (board[r][col] == Player.NONE) {
                return r;
            }
        }
        return -1;
    }


    private int evaluateHorizontalThreats(Player player) {
        int score = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c <= COLS - WIN_CON_LENGTH; c++) {
                score += scoreSegment(r, c, 0, 1, player);
            }
        }
        return score;
    }


    private int evaluateVerticalThreats(Player player) {
        int score = 0;
        for (int r = 0; r <= ROWS - WIN_CON_LENGTH; r++) {
            for (int c = 0; c < COLS; c++) {
                score += scoreSegment(r, c, 1, 0, player);
            }
        }
        return score;
    }

    private int evaluateDiagonalThreats(Player player) {
        int score = 0;
        for (int r = 0; r <= ROWS - WIN_CON_LENGTH; r++) {
            for (int c = 0; c <= COLS - WIN_CON_LENGTH; c++) {
                score += scoreSegment(r, c, 1, 1, player);
            }
        }
        for (int r = WIN_CON_LENGTH - 1; r < ROWS; r++) {
            for (int c = 0; c <= COLS - WIN_CON_LENGTH; c++) {
                score += scoreSegment(r, c, -1, 1, player);
            }
        }
        return score;
    }


    /**
     * Checks if a given column is valid for dropping a piece (i.e., within board boundaries and not full).
     *
     * @param col The column index to check.
     * @return `true` if the column is valid, `false` otherwise.
     */
    public boolean isValidColumn(int col) {
        return col >= 0 && col < COLS && board[0][col] == Player.NONE; // Check if top row is empty
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
     * Determines the winner of the Connect4 game.
     * Checks for four consecutive pieces in horizontal, vertical, and diagonal directions.
     *
     * @return The `BaseGame.Player` who won (HUMAN or AI), or `BaseGame.Player.NONE` if no winner yet or it's a draw.
     */
    @Override
    public Player getWinner() {
        if (checkWinConditionForPlayer(Player.HUMAN)) return Player.HUMAN;
        if (checkWinConditionForPlayer(Player.AI)) return Player.AI;
        return Player.NONE;
    }

    private boolean checkWinConditionForPlayer(Player playerToCheck) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] == playerToCheck) {
                    if (checkHorizontalWinFrom(r, c, playerToCheck)) return true;
                    if (checkVerticalWinFrom(r, c, playerToCheck)) return true;
                    if (checkDiagonalWinFrom(r, c, playerToCheck)) return true;
                }
            }
        }
        return false;
    }

    private boolean checkHorizontalWinFrom(int startR, int startC, Player player) {
        if (startC <= COLS - WIN_CON_LENGTH) {
            return IntStream.range(0, WIN_CON_LENGTH).allMatch(i -> board[startR][startC + i] == player);
        }
        return false;
    }

    private boolean checkVerticalWinFrom(int startR, int startC, Player player) {
        if (startR <= ROWS - WIN_CON_LENGTH) {
            return IntStream.range(0, WIN_CON_LENGTH).allMatch(i -> board[startR + i][startC] == player);
        }
        return false;
    }


    private boolean checkDiagonalWinFrom(int startR, int startC, Player player) {
        if (startR <= ROWS - WIN_CON_LENGTH && startC <= COLS - WIN_CON_LENGTH) {
            if (IntStream.range(0, WIN_CON_LENGTH).allMatch(i -> board[startR + i][startC + i] == player)) return true;
        }
        if (startR >= WIN_CON_LENGTH - 1 && startC <= COLS - WIN_CON_LENGTH) {
            if (IntStream.range(0, WIN_CON_LENGTH).allMatch(i -> board[startR - i][startC + i] == player)) return true;
        }
        return false;
    }


    /**
     * Gets a list of all valid moves (columns that are not full).
     * For Connect4, a move is simply dropping a piece into a column.
     *
     * @return A `List` of `BaseGame.Move` objects, where each move represents a valid column.
     * The row component of the `BaseGame.Move` object will be -1 as it's not relevant for input.
     */
    @Override
    public List<Move> getValidMoves() {
        List<Move> currentValidMoves = new ArrayList<>();
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == Player.NONE) {
                currentValidMoves.add(new Move(-1, col));
            }
        }
        return currentValidMoves;
    }

    public boolean isColumnValidForDrop(int col) {
        return col >= 0 && col < COLS && board[0][col] == Player.NONE;
    }

    /**
     * Evaluates the current state of the Connect4 board for the AI player.
     * This function is crucial for the BaseGame.Minimax algorithm.
     * It assigns a numerical score based on potential winning lines for AI and blocking lines for Human.
     *
     * @return An integer score representing the evaluation of the current board state.
     */

    @Override
    public int evaluate() {
        Player detectedWinner = getWinner();
        if (detectedWinner == Player.AI) return 1000000;
        if (detectedWinner == Player.HUMAN) return -1000000;

        int totalEvaluationScore = 0;
        totalEvaluationScore += calculatePlayerBoardValue(Player.AI);
        totalEvaluationScore -= calculatePlayerBoardValue(Player.HUMAN);
        return totalEvaluationScore;
    }

    private int calculatePlayerBoardValue(Player player) {
        int playerSpecificScore = 0;

        playerSpecificScore += evaluateHorizontalThreats(player);
        playerSpecificScore += evaluateVerticalThreats(player);
        playerSpecificScore += evaluateDiagonalThreats(player);
        playerSpecificScore += evaluateCenterControlValue(player);

        return playerSpecificScore;
    }

    private int evaluateCenterControlValue(Player player) {
        int centerColumnIndex = COLS / 2;
        int centerPieceCount = 0;
        for (int r = 0; r < ROWS; r++) {
            if (board[r][centerColumnIndex] == player) {
                centerPieceCount++;
            }
        }
        return centerPieceCount * 5;
    }


    /**
     * Helper method to evaluate potential lines for a given player.
     * Counts occurrences of 2-in-a-row and 3-in-a-row, considering empty spaces.
     *
     * @param currentPlayer The player whose lines are being evaluated.
     * @return The calculated score based on potential lines.
     */
    private int evaluateLine(Player currentPlayer) {
        int score = 0;

        // Check horizontal
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                score += scoreSegment(r, c, 0, 1, currentPlayer);
            }
        }

        // Check vertical
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 0; c < COLS; c++) {
                score += scoreSegment(r, c, 1, 0, currentPlayer);
            }
        }

        // Check ascending diagonal
        for (int r = 3; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                score += scoreSegment(r, c, -1, 1, currentPlayer);
            }
        }

        // Check descending diagonal
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                score += scoreSegment(r, c, 1, 1, currentPlayer);
            }
        }
        return score;
    }

    /**
     * Scores a segment of 4 cells in a given direction.
     *
     * @param startRow Starting row.
     * @param startCol Starting column.
     * @param deltaRow Row increment.
     * @param deltaCol Column increment.
     * @param targetPlayer The player/AI whose pieces are being counted.
     * @return Score for the segment.
     */
    private int scoreSegment(int startRow, int startCol, int deltaRow, int deltaCol, Player targetPlayer) {
        int currentPlayerCount = 0;
        int emptySlots = 0;
        int opponentPieces = 0;

        for (int i = 0; i < WIN_CON_LENGTH; i++) {
            int currentRow = startRow + i * deltaRow;
            int currentCol = startCol + i * deltaCol;

            Player cellContent = getCell(currentRow, currentCol);

            if (cellContent == targetPlayer) {
                currentPlayerCount++;
            } else if (cellContent == Player.NONE) {
                emptySlots++;
            } else {
                opponentPieces++;
            }
        }

        if (opponentPieces > 0) {
            return 0;
        }

        if (currentPlayerCount == 3 && emptySlots == 1) return 100;
        if (currentPlayerCount == 2 && emptySlots == 2) return 10;
        if (currentPlayerCount == 1 && emptySlots == 3) return 1;

        return 0;
    }


    /**
     * Creates a deep copy of the current Connect4 game state.
     * This is vital for the BaseGame.Minimax algorithm to explore hypothetical moves
     * without modifying the actual game board.
     *
     * @return A new `Connect4` object that is an independent copy.
     */
    @Override
    public Game clone() {
        return new Connect4(this.board, this.currentPlayer, this.moveHistory);
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
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            throw new IndexOutOfBoundsException("Invalid cell coordinates: (" + row + ", " + col + ")");
        }
        return board[row][col];
    }


    public Player getOpponent(Player player) {
        return (player == Player.HUMAN) ? Player.AI : Player.HUMAN;
    }

}
