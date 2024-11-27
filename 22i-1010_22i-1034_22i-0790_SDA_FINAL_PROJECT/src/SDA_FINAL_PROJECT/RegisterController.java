package SDA_FINAL_PROJECT;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<String> userTypeComboBox;

    @FXML
    public void initialize() {
        userTypeComboBox.getItems().addAll("Admin", "Player", "TurfOwner");
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String userType = userTypeComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || userType == null) {
            showAlert("Registration Error", "Please fill in all fields.");
            return;
        }

        try {
            Main.ph.registerUser(username, password, email, userType);

            if ("Player".equals(userType)) {
                String playerQuery = "INSERT INTO Player (username, password, email, ranking, team_id, role) VALUES (?, ?, ?, ?, NULL, 'Player')";
                try (PreparedStatement pstmt = Main.ph.getConnection().prepareStatement(playerQuery)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    pstmt.setString(3, email);
                    pstmt.setInt(4, 1); 
                    pstmt.executeUpdate();
                    System.out.println("New player added to the Players table: " + username);
                }
            }

            if ("TurfOwner".equals(userType)) {
                String turfOwnerQuery = "INSERT INTO TurfOwners (turf_owner_id) VALUES ((SELECT user_id FROM User WHERE username = ?))";
                try (PreparedStatement pstmt = Main.ph.getConnection().prepareStatement(turfOwnerQuery)) {
                    pstmt.setString(1, username);  
                    pstmt.executeUpdate();
                    System.out.println("New turf owner added to the TurfOwners table: " + username);
                }
            }
            showAlert("Success", "User registered successfully!");
            goToLogin();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Registration Error", "Could not register user. Details: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load login screen.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
