package SDA_FINAL_PROJECT;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.*;

public class TurfOwnerDashboardController {

    @FXML
    private Button uploadTurfButton;
    @FXML
    private Button removeTurfButton;
    
    @FXML
    private void handleUploadTurf(ActionEvent event) {
        Dialog<Turf> dialog = new Dialog<>();
        dialog.setTitle("Upload Turf");
        dialog.setHeaderText("Enter Turf Details");

        // Create input fields
        TextField nameField = new TextField();
        nameField.setPromptText("Turf Name");

        TextField locationField = new TextField();
        locationField.setPromptText("Location");

        TextField ratingField = new TextField();
        ratingField.setPromptText("Rating (1-5)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Turf Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Location:"), 0, 1);
        grid.add(locationField, 1, 1);
        grid.add(new Label("Rating:"), 0, 2);
        grid.add(ratingField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType uploadButtonType = new ButtonType("Upload", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(uploadButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == uploadButtonType) {
                try {
                    int rating = Integer.parseInt(ratingField.getText());
                    if (rating < 1 || rating > 5) {
                        throw new NumberFormatException("Rating out of range");
                    }
                    return new Turf(nameField.getText(), locationField.getText());
                } catch (NumberFormatException e) {
                    showalerts("Input Error", "Please enter a valid rating between 1 and 5.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(turf -> {
            try {
                saveTurfToDatabase(turf);
                showalerts("Success", "Turf uploaded successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                showalerts("Database Error", "Failed to upload turf. Please try again.");
            }
        });
    }

    private void saveTurfToDatabase(Turf turf) {
        String insertTurfQuery = "INSERT INTO turf (name, location, rating, availability, turf_owner_id) " +
                                 "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/turf_management", "root", "zayan")) {
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertTurfQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, turf.getName());
                preparedStatement.setString(2, turf.getLocation());
                preparedStatement.setInt(3, turf.getRating());
                preparedStatement.setBoolean(4, turf.isAvailable());
                preparedStatement.setInt(5, SessionManager.getId()); 
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int turfId = generatedKeys.getInt(1);
                        System.out.println("Turf inserted successfully with ID: " + turfId);
                    }
                } else {
                    System.out.println("Failed to insert turf into the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while saving the turf to the database.");
        }
    }


    @FXML
    private void handleRemoveTurf(ActionEvent event) {
        try {
            int currentUserId = SessionManager.getId();
            List<Turf> turfs = getTurfsForUser(currentUserId);

            if (turfs.isEmpty()) {
                showAlert("No Turfs", "You do not have any turfs associated with your account.");
                return;
            }

            StringBuilder turfDetails = new StringBuilder("Turf List:\n");
            for (Turf turf : turfs) {
                turfDetails.append("Turf ID: ").append(turf.getId())
                           .append(" - Name: ").append(turf.getName())
                           .append(" - Location: ").append(turf.getLocation())
                           .append("\n");
            }

            String turfIdInput = showTextInputDialog(turfDetails.toString());

            if (turfIdInput == null || turfIdInput.isEmpty()) {
                showAlert("Error", "Please enter a valid turf ID.");
                return;
            }

            try {
                int turfIdToRemove = Integer.parseInt(turfIdInput);
                Turf turfToRemove = turfs.stream()
                                         .filter(turf -> turf.getId() == turfIdToRemove)
                                         .findFirst()
                                         .orElse(null);

                if (turfToRemove != null) {
                    removeTurfFromDatabase(turfToRemove.getId());
                    showAlert("Success", "Turf removed successfully.");
                } else {
                    showAlert("Error", "No turf found with the given ID.");
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid turf ID. Please enter a valid number.");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error fetching turfs.");
        }
    }

    private String showTextInputDialog(String promptText) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Turf ID");
        dialog.setHeaderText("Please enter the Turf ID to remove:");
        dialog.setContentText(promptText);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void removeTurfFromDatabase(int turfId) throws SQLException {
        String query = "DELETE FROM turf WHERE turf_id = ?";
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/turf_management", "root", "zayan");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setInt(1, turfId);
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("No turf found with the given ID.");
            }
        }
    }

    private List<Turf> getTurfsForUser(int userId) throws SQLException {
    	List<Turf> turfs = new ArrayList<>();
    	String query = "SELECT * FROM turf WHERE turf_owner_id = ?";
    	
    	try (Connection connection = DriverManager.getConnection(
    			"jdbc:mysql://localhost:3306/turf_management", "root", "zayan");
    			PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    		
    		preparedStatement.setInt(1, userId);
    		ResultSet resultSet = preparedStatement.executeQuery();
    		
    		while (resultSet.next()) {
    			Turf turf = new Turf();
    			turf.setId(resultSet.getInt("turf_id"));
    			turf.setName(resultSet.getString("name"));
    			turf.setLocation(resultSet.getString("location"));
    			turfs.add(turf);
    		}
    	}
    	
    	return turfs;
    }

    private void showalerts(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
