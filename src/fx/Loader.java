package fx;

import java.net.URL;
import java.io.FileNotFoundException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class Loader {
    private static final String FXML_PATH = "resources/";

    public static Pane getPage(String filename) {
        try {
            URL fileURL = Loader.class.getResource(FXML_PATH + filename + ".fxml");
            if (fileURL == null) {
                throw new FileNotFoundException("FXML File not Found: " + filename);
            }
            FXMLLoader loader = new FXMLLoader(fileURL);
            return loader.load();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
