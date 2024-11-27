package SDA_FINAL_PROJECT;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> userTypeComboBox;
    @FXML
    private Button loginButton;

    private MainMenu mainMenu; 

    @FXML
    public void initialize() {
        userTypeComboBox.getItems().addAll("Admin", "Player", "TurfOwner");
    }

    public void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }@FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String userType = userTypeComboBox.getValue();

        if (userType == null || username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Please fill in all fields and select a valid user type.");
            return;
        }

        try {
            if (Main.ph.authenticateUser(username, password, userType)) {
                System.out.println("Login successful for user: " + username);

                if ("Player".equals(userType)) {
                    Player player = new Player(username, password, username + "@example.com", 0); 
                    SessionManager.setCurrentPlayer(player);
                    loadPlayerDashboard(player);
                } else if ("Admin".equals(userType)) {
                    openAdminDashboard();
                } else if ("TurfOwner".equals(userType)) {
                    TurfOwner turfOwner = new TurfOwner(username, password, username + "@example.com");
                    SessionManager.setCurrentTurfOwner(turfOwner);
                    openTurfOwnerDashboard();
                }
            } else {
                showAlert("Login Error", "Invalid username, password, or user type.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while accessing the database.");
        }
    }
    
    @FXML
    private void openRegisterScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Scene registerScene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(registerScene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load registration screen.");
        }
    }

    private void loadPlayerDashboard(Player player) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Player Dashboard.fxml"));
        Scene playerDashboardScene = new Scene(loader.load());

        PlayerDashboardController playerDashboardController = loader.getController();
        playerDashboardController.setMainMenu(mainMenu);
        playerDashboardController.initialize(player, player); 

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(playerDashboardScene);
        stage.show();
    }

    private void openAdminDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin Dashboard.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Admin Dashboard");
        stage.show();
    }

    private void openTurfOwnerDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TurfOwnerDashboard.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Turf Owner Dashboard");
        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
