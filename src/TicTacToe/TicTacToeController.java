package TicTacToe;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import BaseGame.*;
import java.io.IOException;

/**
 * Controller for the Tic-Tac-Toe game UI.
 * Manages user interactions, updates the game board visually, and integrates with the BaseGame.Minimax AI.
 */
public class TicTacToeController {

    @FXML
    private GridPane boardGrid; // The GridPane representing the Tic-Tac-Toe board

    @FXML
    private Label statusLabel; // Label to display game status (e.g., "BaseGame.Player X's Turn", "AI Wins!")

    private TicTacToe game; // The Tic-Tac-Toe game model

    private static final int AI_DEPTH = 6; // Depth for BaseGame.Minimax algorithm. Higher depth = smarter AI, slower moves.

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is automatically called by FXMLLoader.
     */
    @FXML
    public void initialize() {
        // Initialize a new Tic-Tac-Toe game
        game = new TicTacToe();
        // Draw the initial empty board
        drawBoard();
        // Update the status label to show the current player
        updateStatus();
    }

    /**
     * Draws the Tic-Tac-Toe board visually based on the current game state.
     * Clears existing board elements and redraws 'X' or 'O' for each occupied cell.
     */
    private void drawBoard() {
        boardGrid.getChildren().clear(); // Clear existing UI elements
        for (int row = 0; row < TicTacToe.BOARD_SIZE; row++) {
            for (int col = 0; col < TicTacToe.BOARD_SIZE; col++) {
                // Create a StackPane for each cell to hold the 'X' or 'O' and handle clicks
                StackPane cell = new StackPane();
                cell.setPrefSize(150, 150); // Set preferred size for each cell
                cell.setStyle("-fx-border-color: #34495e; -fx-border-width: 2; -fx-background-color: #ecf0f1;"); // Cell styling

                final int r = row; // Final variables for use in lambda expression
                final int c = col;

                // Add click event handler for each cell
                cell.setOnMouseClicked(event -> handleCellClick(r, c));

                // Get the player occupying the current cell
                Player player = game.getCell(row, col);

                // Draw 'X' or 'O' based on the player
                if (player == Player.HUMAN) {
                    drawX(cell); // Draw 'X' for human player
                } else if (player == Player.AI) {
                    drawO(cell); // Draw 'O' for AI player
                }

                // Add the cell (StackPane) to the GridPane at the correct row and column
                boardGrid.add(cell, col, row);
            }
        }
    }

    /**
     * Handles a click event on a Tic-Tac-Toe cell.
     * Processes the human player's move, updates the game state, and triggers AI's turn if applicable.
     *
     * @param row The row index of the clicked cell.
     * @param col The column index of the clicked cell.
     */
    private void handleCellClick(int row, int col) {
        // Only allow moves if the game is not over and it's the human player's turn
        if (!game.isGameOver() && game.getCurrentPlayer() == Player.HUMAN) {
            Move humanMove = new Move(row, col);
            if (game.isValidMove(humanMove)) {
                // Make the human's move
                game.makeMove(humanMove, Player.HUMAN);
                // Redraw the board to reflect the human's move
                drawBoard();
                // Update game status
                updateStatus();

                // If the game is not over after human's move, let AI make a move after a short delay
                if (!game.isGameOver()) {
                    PauseTransition pause = new PauseTransition(Duration.seconds(0.5)); // Short delay for AI
                    pause.setOnFinished(event -> aiMove());
                    pause.play();
                }
            } else {
                statusLabel.setText("Invalid move! Cell already taken.");
            }
        }
    }

    /**
     * Initiates the AI's turn.
     * Uses the BaseGame.Minimax algorithm to find the best move and applies it to the game.
     */
    private void aiMove() {
        // Set status to indicate AI is thinking
        statusLabel.setText("AI is thinking...");

        // Find the best move for the AI using BaseGame.Minimax
        // The AI is the maximizing player
        Move aiMove = Minimax.findBestMove(game, AI_DEPTH, Player.AI);

        if (aiMove != null && game.isValidMove(aiMove)) {
            // Make the AI's move
            game.makeMove(aiMove, Player.AI);
            // Redraw the board to reflect the AI's move
            drawBoard();
            // Update game status
            updateStatus();
        } else {
            // This case should ideally not be reached if AI logic is correct and game is not over.
            System.err.println("AI could not find a valid move or game is already over.");
            updateStatus(); // Update status to reflect game end if AI found no move.
        }
    }

    /**
     * Updates the status label to display the current game state (e.g., current player, winner, draw).
     */
    private void updateStatus() {
        if (game.isGameOver()) {
            Player winner = game.getWinner();
            if (winner != Player.NONE) {
                statusLabel.setText(winner + " Wins!");
            } else {
                statusLabel.setText("It's a Draw!");
            }
            // Disable further clicks on the board when game is over
            boardGrid.setDisable(true);
        } else {
            statusLabel.setText(game.getCurrentPlayer() + "'s Turn");
            // Enable board clicks if game is ongoing
            boardGrid.setDisable(false);
        }
    }

    /**
     * Draws an 'X' symbol inside a given StackPane (cell).
     *
     * @param cell The StackPane representing the cell where 'X' should be drawn.
     */
    private void drawX(StackPane cell) {
        // Create two lines for the 'X'
        Line line1 = new Line(20, 20, 130, 130); // Diagonal from top-left to bottom-right
        Line line2 = new Line(130, 20, 20, 130); // Diagonal from top-right to bottom-left

        // Set stroke properties for the lines
        line1.setStroke(Color.BLUE);
        line2.setStroke(Color.BLUE);
        line1.setStrokeWidth(8);
        line2.setStrokeWidth(8);

        // Add lines to the cell
        cell.getChildren().addAll(line1, line2);
    }

    /**
     * Draws an 'O' symbol inside a given StackPane (cell).
     *
     * @param cell The StackPane representing the cell where 'O' should be drawn.
     */
    private void drawO(StackPane cell) {
        // Create a circle for the 'O'
        Circle circle = new Circle(75, 75, 50); // Center at (75,75), radius 50
        circle.setStroke(Color.RED);
        circle.setStrokeWidth(8);
        circle.setFill(Color.TRANSPARENT); // Make the inside transparent

        // Add circle to the cell
        cell.getChildren().add(circle);
    }

    /**
     * Handles the action when the "New Game" button is clicked.
     * Resets the game state and redraws the board.
     */
    @FXML
    protected void onNewGameButtonClick() {
        initialize(); // Re-initialize the game to start a new one
    }

    /**
     * Handles the action when the "Back to Menu" button is clicked.
     * Returns to the main game chooser screen.
     *
     * @param event The ActionEvent generated by the button click.
     */
    @FXML
    protected void onBackToMenuButtonClick(ActionEvent event) {
        try {
            // Get the current stage from the button that triggered the event.
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Load the FXML file for the game chooser view.
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("../GameChoiceUI/game-chooser-view.fxml"));

            // Create a new scene with the loaded game chooser UI.
            Scene scene = new Scene(fxmlLoader.load(), 600, 400); // Match initial scene dimensions

            // Set the title of the stage.
            stage.setTitle("Game Chooser");

            // Set the new scene on the stage.
            stage.setScene(scene);

            // Show the updated stage.
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading game chooser view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
