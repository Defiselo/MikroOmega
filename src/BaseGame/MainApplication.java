package BaseGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the BaseGame.Game Chooser.
 * This class extends `javafx.application.Application` and serves as the entry point
 * for the JavaFX application. It sets up the primary stage and loads the initial
 * game chooser view.
 */
public class MainApplication extends Application {

    /**
     * The `start` method is the main entry point for all JavaFX applications.
     * It is called after the `init` method (which is typically not overridden).
     *
     * @param stage The primary stage for this application, onto which the application scene can be set.
     * The primary stage is created by the platform.
     * @throws IOException If the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/GameChoiceUI/game-chooser-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("BaseGame.Game Chooser");
        stage.setScene(scene);
        stage.show();
    }
}