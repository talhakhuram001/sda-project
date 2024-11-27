package SDA_FINAL_PROJECT;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class AdminDashboardController {

    @FXML
    private Button removeTurfButton;
    @FXML
    private Button removePlayerButton;
    @FXML
    private Button viewTurfButton;
    @FXML
    private Button viewPlayersButton;

    @FXML
    private void handleRemoveTurf() {
        showAlert("Remove Turf", "Remove Turf functionality not implemented.");
    }

    @FXML
    private void handleRemovePlayer() {
        showAlert("Remove Player", "Remove Player functionality not implemented.");
    }

    @FXML
    private void handleViewTurf() {
        showAlert("View Turf", "View Turf functionality not implemented.");
    }

    @FXML
    private void handleViewPlayers() {
        showAlert("View Players", "View Players functionality not implemented.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
