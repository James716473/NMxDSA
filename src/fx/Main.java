package fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML from the correct location
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/Untitled.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Numerical Methods Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
