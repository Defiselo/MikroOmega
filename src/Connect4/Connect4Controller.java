package Connect4;

import javafx.animation.FillTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import BaseGame.*;
import java.io.IOException;
import java.util.Optional;


/**
 * Controller for the Connect4 game UI.
 * Manages user interactions, visual updates of the board (including piece dropping animation),
 * and integrates with the BaseGame.Minimax AI for AI moves.
 */
public class Connect4Controller {

    @FXML
    private GridPane boardGrid;
    @FXML
    private Label statusLabel;

    private Connect4 game;
    private static final int AI_DEPTH = 9; // Depth for BaseGame.Minimax algorithm. Higher depth = smarter AI, slower moves.
    private static final int AI_MOVE_DELAY = 1;
    private static final int PIECE_DROP_DURATION = 1;
    private static double PIECE_FILL_DURATION = 0.3;
    private boolean isProcessingMove = false;

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is automatically called by FXMLLoader.
     */
    @FXML
    public void initialize() {
        game = new Connect4();
        initializeBoardUI();
        updateStatusDisplay();
        setBoardInteraction(true);
        isProcessingMove = false;
    }

    /**
     * Draws the initial empty Connect4 board with empty circles.
     * This method sets up the visual grid where pieces will be dropped.
     */
    private void initializeBoardUI() {
        boardGrid.getChildren().clear();
        for (int row = 0; row < Connect4.ROWS; row++) {
            for (int col = 0; col < Connect4.COLS; col++) {
                StackPane cellPane = createCellPane(row, col);
                boardGrid.add(cellPane, col, row);
            }
        }
    }


    private Color getPlayerColor(Player player) {
        if (player == Player.HUMAN) {
            return Color.BLUE;
        } else if (player == Player.AI) {
            return Color.RED;
        }
        return Color.LIGHTGRAY;
    }


    private void updateStatusDisplay() {
        if (game.isGameOver()) {
            Player winner = game.getWinner();
            if (winner != Player.NONE) {
                statusLabel.setText(winner + " Wins!");
            } else {
                statusLabel.setText("It's a Draw!");
            }
        } else {
            statusLabel.setText(game.getCurrentPlayer() + "'s Turn");
        }
    }

    private void setBoardInteraction(boolean isEnabled) {
        boardGrid.setDisable(!isEnabled);
    }

    private Optional<Circle> getCircleInCell(int row, int col) {
        return boardGrid.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col)
                .findFirst()
                .map(node -> ((StackPane) node).getChildren().stream()
                        .filter(child -> child instanceof Circle)
                        .map(child -> (Circle) child)
                        .findFirst())
                .orElse(Optional.empty());
    }

    /**
     * Method used for created specific panes on the connect4 board
     * @param row The row in which the cell is generated
     * @param col The column in which the cell is generated
     * @return Returns the generated cell
     */
    private StackPane createCellPane(int row, int col) {
        StackPane cellPane = new StackPane();
        cellPane.setPrefSize(80, 80);

        Circle slotCircle = new Circle(35);
        slotCircle.setFill(Color.LIGHTGRAY);
        slotCircle.setStroke(Color.DARKGRAY);
        slotCircle.setStrokeWidth(2);
        cellPane.getChildren().add(slotCircle);

        final int c = col;
        cellPane.setOnMouseClicked(event -> handleColumnClick(c));

        return cellPane;
    }
    /**
     * Handles a click event on a Connect4 column.
     * Processes the human player's move, updates the game state, and triggers AI's turn if applicable.
     *
     * @param col The column index where the human player wants to drop a piece.
     */
    private void handleColumnClick(int col) {
        if (isProcessingMove || game.isGameOver() || game.getCurrentPlayer() != Player.HUMAN) {
            return;
        }

        if (!game.isValidColumn(col)) {
            statusLabel.setText("Column " + (col + 1) + " is full.");
            return;
        }

        isProcessingMove = true;
        setBoardInteraction(false);

        int targetRow = game.findNextAvailableRow(col);
        if (targetRow == -1) {
            statusLabel.setText("Error: Column is unexpectedly full.");
            isProcessingMove = false;
            setBoardInteraction(true);
            return;
        }

        Move humanMove = new Move(targetRow, col);
        animatePieceDrop(targetRow, col, Player.HUMAN, () -> {
            game.makeMove(humanMove, Player.HUMAN);
            updateStatusDisplay();
            handleNextTurn();
        });
    }


    private void handleNextTurn() {
        if (!game.isGameOver()) {
            if (game.getCurrentPlayer() == Player.AI) {
                PauseTransition pause = new PauseTransition(Duration.seconds(AI_MOVE_DELAY));
                pause.setOnFinished(event -> aiMove());
                pause.play();
            } else {
                isProcessingMove = false;
                setBoardInteraction(true);
            }
        } else {
            isProcessingMove = false;
            setBoardInteraction(true);
        }
    }

    private void animateCellFill(int row, int col, Player player, Runnable onFinishedCallback) {
        Optional<Circle> targetCircleOpt = getCircleInCell(row, col);
        if (targetCircleOpt.isPresent()) {
            Circle circleToFill = targetCircleOpt.get();
            FillTransition fillTransition = new FillTransition(Duration.seconds(PIECE_FILL_DURATION), circleToFill, (Color) circleToFill.getFill(), getPlayerColor(player));
            fillTransition.setOnFinished(event -> onFinishedCallback.run());
            fillTransition.play();
        } else {
            onFinishedCallback.run();
        }
    }


    /**
     * Finds the lowest available row in a given column where a piece can be dropped.
     *
     * @param col The column index.
     * @return The row index where the piece would land, or -1 if the column is full.
     */
    private int findNextAvailableRow(int col) {
        for (int row = Connect4.ROWS - 1; row >= 0; row--) {
            if (game.getCell(row, col) == Player.NONE) {
                return row;
            }
        }
        return -1; // Column is full
    }

    private StackPane getCellPaneAt(int row, int col) {
        return (StackPane) boardGrid.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col)
                .findFirst()
                .orElse(null);
    }

    /**
     * Initiates the AI's turn.
     * Uses the BaseGame.Minimax algorithm to find the best move and applies it to the game with animation.
     */
    private void aiMove() {
        statusLabel.setText("AI is thinking...");
        boardGrid.setDisable(true); // Disable board interaction while AI thinks

        // Run AI calculation in a separate thread to keep UI responsive
        new Thread(() -> {
            // The AI is the maximizing player
            Move aiMove = Minimax.findBestMove(game, AI_DEPTH, Player.AI);

            // Update UI on the JavaFX Application Thread
            javafx.application.Platform.runLater(() -> {
                if (aiMove != null && game.isValidColumn(aiMove.col())) {
                    // Find the actual row where the AI's piece will land
                    int targetRow = findNextAvailableRow(aiMove.col());
                    if (targetRow != -1) {
                        Move actualAiMove = new Move(targetRow, aiMove.col()); // Create move with actual landing row

                        // Animate the piece dropping
                        animatePieceDrop(targetRow, actualAiMove.col(), Player.AI, () -> {
                            // Callback after animation completes
                            game.makeMove(actualAiMove, Player.AI); // Make the move in the game model
                            updateStatus(); // Update game status
                            isProcessingMove = false;
                            boardGrid.setDisable(false); // Re-enable board interaction
                        });
                    } else {
                        // This case should ideally not be reached if AI logic is correct and column was valid.
                        System.err.println("AI tried to drop in a full column.");
                        updateStatus();
                        isProcessingMove = false;
                        boardGrid.setDisable(false);
                    }
                } else {
                    System.err.println("AI could not find a valid move or game is already over.");
                    updateStatus();
                    isProcessingMove = false;
                    boardGrid.setDisable(false);
                }
            });
        }).start();

    }

    /**
     * Animates a piece dropping into a specific cell and then updates the cell's color.
     *
     * @param targetRow The final row where the piece will land.
     * @param targetCol The column where the piece is dropped.
     * @param player The player whose piece is being dropped.
     */
    private void animatePieceDrop(int targetRow, int targetCol, Player player, Runnable onFinishedCallback) {
        Circle fallingPiece = new Circle(35);
        fallingPiece.setFill(getPlayerColor(player));

        double cellHeight = boardGrid.getCellBounds(0, 0).getHeight();
        fallingPiece.setTranslateY(-cellHeight * (Connect4.ROWS + 1));

        StackPane topCellInColumn = getCellPaneAt(0, targetCol);
        if (topCellInColumn == null) {
            onFinishedCallback.run();
            return;
        }
        topCellInColumn.getChildren().add(fallingPiece);

        double dropDistance = targetRow * cellHeight;
        TranslateTransition dropTransition = new TranslateTransition(Duration.seconds(PIECE_DROP_DURATION), fallingPiece);
        dropTransition.setToY(dropDistance);

        dropTransition.setOnFinished(e -> {
            topCellInColumn.getChildren().remove(fallingPiece);
            animateCellFill(targetRow, targetCol, player, onFinishedCallback);
        });
        dropTransition.play();
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
            boardGrid.setDisable(true); // Disable further interaction
        } else {
            statusLabel.setText(game.getCurrentPlayer() + "'s Turn");
            boardGrid.setDisable(false); // Enable interaction
        }
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
