package fx;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;

public class SolutionController {
    @FXML
    private TextArea textArea;

    @FXML
    public void initialize() {
        int maxRows = 10;
        int maxColumns = 30;

        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            String[] lines = newText.split("\n", -1);
            if (lines.length > maxRows)
                return null; // Reject if too many rows

            for (String line : lines) {
                if (line.length() > maxColumns)
                    return null; // Reject if any line exceeds max columns
            }

            return change; // Accept
        });

        textArea.setTextFormatter(formatter);
    }
}
