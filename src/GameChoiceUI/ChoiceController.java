package GameChoiceUI;

import BaseGame.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the Game Chooser view.
 * This class handles user interactions on the initial screen where the user selects
 * between playing Tic-Tac-Toe and Connect4. It manages the loading of the respective
 * game views.
 */
public class ChoiceController {

    /**
     * Handles the action when the "Play Tic-Tac-Toe" button is clicked.
     * Loads the Tic-Tac-Toe game view and sets it as the current scene.
     *
     * @param event The ActionEvent generated by the button click.
     */
    @FXML
    protected void onTicTacToeButtonClick(ActionEvent event) {
        try {
            // Get the current stage from the button that triggered the event.
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Load the FXML file for the Tic-Tac-Toe game view.
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("../TicTacToe/tictactoe-view.fxml"));

            // Create a new scene with the loaded Tic-Tac-Toe UI.
            // Set preferred dimensions for the Tic-Tac-Toe game.
            Scene scene = new Scene(fxmlLoader.load(), 700, 750);

            // Set the title of the stage to reflect the current game.
            stage.setTitle("Tic-Tac-Toe");

            // Set the new scene on the stage.
            stage.setScene(scene);

            // Show the updated stage.
            stage.show();
        } catch (IOException e) {
            // Print stack trace if there's an error loading the FXML.
            System.err.println("Error loading Tic-Tac-Toe view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the "Play Connect4" button is clicked.
     * Loads the Connect4 game view and sets it as the current scene.
     *
     * @param event The ActionEvent generated by the button click.
     */
    @FXML
    protected void onConnect4ButtonClick(ActionEvent event) {
        try {
            // Get the current stage from the button that triggered the event.
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Load the FXML file for the Connect4 game view.
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("../Connect4/connect4-view.fxml"));

            // Create a new scene with the loaded Connect4 UI.
            // Set preferred dimensions for the Connect4 game.
            Scene scene = new Scene(fxmlLoader.load(), 800, 750);

            // Set the title of the stage to reflect the current game.
            stage.setTitle("Connect4");

            // Set the new scene on the stage.
            stage.setScene(scene);

            // Show the updated stage.
            stage.show();
        } catch (IOException e) {
            // Print stack trace if there's an error loading the FXML.
            System.err.println("Error loading Connect4 view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

